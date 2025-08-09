/*****************************************************************
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........:
* Ultima alteracao.:
* Nome.............: CamadaEnlaceDadosReceptora
* Funcao...........: Transfere a mensagem codificada do meio de comunicacao para camadafisica receptora
*************************************************************** */
package model;

import java.util.Map;
import java.util.HashMap;
import controller.TelaPrincipalController;
import utils.FuncoesAuxiliares;

public class CamadaEnlaceDadosReceptora {
  FuncoesAuxiliares auxiliar = new FuncoesAuxiliares(); // Cria objeto de funcoes auxiliares para termos rapidez na hora de programar
  private static int proximoNumEsperado = 0; // Variavel estatica para rastrear a sequencia esperada
  private static Map<Integer, int[]> bufferRecepcao = new HashMap<>(); // Buffer para retransmissao seletiva
  private static final int TAMANHO_JANELA_RS = 4; // tamanho da janela da retransmissao seletiva
  private boolean errorOccured = false; // Variavel para ser uma flag de erros

/**************************************************************
* Metodo: receber
* Funcao: recebe os bits e passa eles para camada seguinte já desenquadrados
* @param int[] quadroEnquadrado | bits recebidos
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return void
* ********************************************************* */
public void receber(int[] quadroEnquadrado, TelaPrincipalController controller) {
  String enquadramento = controller.getComboBoxEnquadramento();
  String fluxo = controller.getComboBoxControleFluxo();

  if (auxiliar.isQuadroAck(quadroEnquadrado)) return;

  int[] quadroDesenquadrado = desenquadrar(quadroEnquadrado, enquadramento, controller);
  int[] quadroVerificado = controleErro(quadroDesenquadrado, controller);

  if (quadroVerificado != null && quadroVerificado.length > 0) 
  {
    if(errorOccured == true)
    {
      controller.emitirErro("Um erro ocorreu, mas foi devidamente tratado");
    }
      int numeroSequencia = auxiliar.extrairNumeroDeSequencia(quadroVerificado);

    //Escolhe o tipo de controle de fluxo
    switch (fluxo) 
    {
      case "Retransmissão Seletiva":
          // Verifica se o quadro está dentro da janela de recepção
          if (numeroSequencia >= proximoNumEsperado && numeroSequencia < proximoNumEsperado + TAMANHO_JANELA_RS) {
              int[] dados = auxiliar.extrairDados(quadroVerificado);
              bufferRecepcao.put(numeroSequencia, dados); // Guarda no buffer mesmo que fora de ordem
              enviarAck(numeroSequencia, controller); // Envia ACK para o quadro que chegou

              // Tenta entregar quadros em sequência a partir da base
              while (bufferRecepcao.containsKey(proximoNumEsperado)) {
                  int[] dadosParaEntregar = bufferRecepcao.remove(proximoNumEsperado);
                  CamadaAplicacaoReceptora camadaAppRx = new CamadaAplicacaoReceptora();
                  camadaAppRx.receber(dadosParaEntregar, controller);
                  proximoNumEsperado++; // Desliza a janela de recepção
              }
          } else if (numeroSequencia < proximoNumEsperado) {
              // Quadro duplicado, o ACK pode ter se perdido. Reenvia o ACK.
              enviarAck(numeroSequencia, controller);
          }
          break;

      case "Deslizante Go-Back-N":
      case "Deslizante 1 bit":
      default: // A lógica para "Go-Back-N" e "1 bit" é a mesma neste trecho
        if (numeroSequencia == proximoNumEsperado) {
            int[] dados = auxiliar.extrairDados(quadroVerificado);
            CamadaAplicacaoReceptora camadaAppRx = new CamadaAplicacaoReceptora();
            camadaAppRx.receber(dados, controller);
            proximoNumEsperado++;
            enviarAck(proximoNumEsperado, controller); // ACK para o PRÓXIMO esperado
        } else {
            // Apenas o Go-Back-N precisa reenviar o último ACK em caso de quadro fora de ordem
            if ("Deslizante Go-Back-N".equals(fluxo)) {
                enviarAck(proximoNumEsperado, controller); // Reenvia ACK do último em ordem
            }
        }
        break;
    } // Fim do switch
  } else {
      System.out.println("RECEPTOR: Erro detectado no quadro. Descartando.");
      errorOccured = true;
      System.out.println(errorOccured);
  }
  } // FIm do metodo

/**************************************************************
* Metodo: enviarAck
* Funcao: Cria e envia um quadro de ACK de volta pelo meio de comunicacao.
* @param int proximoEsperado | O numero do proximo quadro que o receptor espera.
* @param TelaPrincipalController controller | O controller da GUI.
* @return void
* ********************************************************* */
  private void enviarAck(int numeroAck, TelaPrincipalController controller) {
    //System.out.println("RECEPTOR: Preparando para enviar ACK para o proximo quadro esperado: " + proximoEsperado);
    int[] ackFrame = auxiliar.criarQuadroAck(numeroAck);

    // O ACK tambem precisa passar pela camada fisica para ser "enviado"
    String codificacao = controller.getComboBoxCodificacao();
    CamadaFisicaTransmissora fisicaTx = new CamadaFisicaTransmissora();
    // O receptor usa a CamadaFisicaTransmissora para enviar o ACK de volta.
    fisicaTx.transmitir(ackFrame, codificacao, controller);
  }

/**************************************************************
* Metodo: reset
* Funcao: zera o contador de sequencia para uma nova simulacao
* @return void
* ********************************************************* */
  public static void reset() {
    proximoNumEsperado = 0;
  }

/**************************************************************
* Metodo: desenquadrar
* Funcao: desenquadra os bits e passa eles para camada seguinte
* @param int[] quadroEnquadrado | bits recebidos enquadrados
* @param String enquadramento | enquadramento escolhida
* @return int[] quadroDesenquadrado | quadro de bits desenquadrado
* ********************************************************* */
  private int[] desenquadrar(int[] quadroEnquadrado, String enquadramento, TelaPrincipalController controller)
  {
    if(auxiliar.isQuadroAck(quadroEnquadrado)) return quadroEnquadrado;

    int[] quadroDesenquadrado;
    switch (enquadramento) {
        case "Contagem de Caracteres": quadroDesenquadrado = auxiliar.desenquadroContagemCaracteres(quadroEnquadrado); break;
        case "Inserção de Bytes": quadroDesenquadrado = auxiliar.desenquadroInsercaoBytes(quadroEnquadrado); break;
        case "Inserção de Bits": quadroDesenquadrado = auxiliar.desenquadroInsercaoBits(quadroEnquadrado); break;
        case "Violação da Camada Física": quadroDesenquadrado = auxiliar.desenquadroViolacaoFisica(quadroEnquadrado); break;
        default: return auxiliar.desenquadroContagemCaracteres(quadroEnquadrado);
    }

    String controleErro = controller.getComboBoxControleErro();
    // FIX: Remove o cabeçalho de comprimento e o preenchimento para combinações incompatíveis
    if (enquadramento.equals("Inserção de Bytes") &&
      (controleErro.equals("Código de Hamming") || controleErro.equals("Paridade Par") || controleErro.equals("Paridade Impar"))) {

        if (quadroDesenquadrado != null && quadroDesenquadrado.length >= 16) {
            // Lê o cabeçalho de 16 bits para descobrir o comprimento original
            StringBuilder binarioDoTamanho = new StringBuilder();
            for(int i=0; i<16; i++) {
                binarioDoTamanho.append(quadroDesenquadrado[i]);
            }
            int originalLength = Integer.parseInt(binarioDoTamanho.toString(), 2);

            // Extrai apenas os dados válidos, descartando o preenchimento
            if (quadroDesenquadrado.length >= 16 + originalLength) {
                return java.util.Arrays.copyOfRange(quadroDesenquadrado, 16, 16 + originalLength);
            } else {
                return null; // Erro de enquadramento, quadro menor que o esperado
            }
        } else {
          return null; // Erro, quadro muito curto para conter o cabeçalho
        }
    }

    return quadroDesenquadrado;
  }

/**************************************************************
* Metodo: controleErro
* Funcao: Trata o erro com o valor escolhido
* @param int[] quadroDeBits | bits recebidos
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return int[] quadroDeBitsVerificado | quadro de bits limpo de erros
* ********************************************************* */
  private int[] controleErro(int[] quadroDeBits, TelaPrincipalController controller)
  {
    String controleErro = controller.getComboBoxControleErro();
    switch (controleErro) {
        case "Paridade Par": return auxiliar.paridadeParVerificacao(quadroDeBits, controller);
        case "Paridade Impar": return auxiliar.paridadeImparVerificacao(quadroDeBits, controller);
        case "CRC": return auxiliar.crcVerificacao(quadroDeBits, controller);
        case "Código de Hamming": return auxiliar.hammingVerificacao(quadroDeBits, controller);
        default: return auxiliar.paridadeParVerificacao(quadroDeBits, controller);
    }
  }
}