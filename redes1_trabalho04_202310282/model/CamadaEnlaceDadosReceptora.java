/*****************************************************************
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........:
* Ultima alteracao.:
* Nome.............: CamadaEnlaceDadosReceptora
* Funcao...........: Transfere a mensagem codificada do meio de comunicacao para camadafisica receptora
*************************************************************** */
package model;

import controller.TelaPrincipalController;
import utils.FuncoesAuxiliares;

public class CamadaEnlaceDadosReceptora {
  FuncoesAuxiliares auxiliar = new FuncoesAuxiliares(); // Cria objeto de funcoes auxiliares para termos rapidez na hora de programar
  private static int proximoNumEsperado = 0; // Variavel estatica para rastrear a sequencia esperada

/**************************************************************
* Metodo: receber
* Funcao: recebe os bits e passa eles para camada seguinte já desenquadrados
* @param int[] quadroEnquadrado | bits recebidos
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return void
* ********************************************************* */
  public void receber(int[] quadroEnquadrado, TelaPrincipalController controller)
  {
    String enquadramento = controller.getComboBoxEnquadramento();
    String fluxo = controller.getComboBoxControleFluxo();

    // Se um ACK chegou aqui, e um erro, apenas ignoramos.
    if(auxiliar.isQuadroAck(quadroEnquadrado)) return;

    int[] quadroDesenquadrado = desenquadrar(quadroEnquadrado, enquadramento);
    int[] quadroVerificado = controleErro(quadroDesenquadrado, controller);

    if (quadroVerificado != null && quadroVerificado.length > 0) {
        int numeroSequencia = auxiliar.extrairNumeroDeSequencia(quadroVerificado);

        // Verifica se o quadro recebido e o esperado
        if(numeroSequencia == proximoNumEsperado) {
          //System.out.println("RECEPTOR: Quadro " + numeroSequencia + " recebido corretamente.");
          int[] dados = auxiliar.extrairDados(quadroVerificado);
          
          CamadaAplicacaoReceptora camadaAppRx = new CamadaAplicacaoReceptora();
          camadaAppRx.receber(dados, controller);

          proximoNumEsperado++; // Atualiza o proximo quadro que esperamos

          // Envia o ACK confirmando que recebeu o quadro e agora espera o proximo.
          enviarAck(proximoNumEsperado, controller);
        } else {
          //System.out.println("RECEPTOR: Quadro " + numeroSequencia + " fora de ordem. Esperando " + proximoNumEsperado + ". Descartando.");
          // No Go-Back-N, quando um quadro fora de ordem chega, o receptor reenvia o ACK do ultimo quadro que ele recebeu corretamente
          // para informar ao transmissor qual e o proximo que ele realmente espera.
          if ("Deslizante Go-Back-N".equals(fluxo)) {
             enviarAck(proximoNumEsperado, controller);
          }
        }
    } else {
      // Se o quadro veio com erro, ele e descartado e nenhum ACK e enviado.
      // Isso forca o timeout do lado do transmissor.
      System.out.println("RECEPTOR: Erro detectado no quadro. Descartando e nao enviando ACK.");
    }
  }

/**************************************************************
* Metodo: enviarAck
* Funcao: Cria e envia um quadro de ACK de volta pelo meio de comunicacao.
* @param int proximoEsperado | O numero do proximo quadro que o receptor espera.
* @param TelaPrincipalController controller | O controller da GUI.
* @return void
* ********************************************************* */
  private void enviarAck(int proximoEsperado, TelaPrincipalController controller) {
    //System.out.println("RECEPTOR: Preparando para enviar ACK para o proximo quadro esperado: " + proximoEsperado);
    int[] ackFrame = auxiliar.criarQuadroAck(proximoEsperado);

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
  private int[] desenquadrar(int[] quadroEnquadrado, String enquadramento)
  {
    if(auxiliar.isQuadroAck(quadroEnquadrado)) return quadroEnquadrado;
    switch (enquadramento) {
        case "Contagem de Caracteres": return auxiliar.desenquadroContagemCaracteres(quadroEnquadrado);
        case "Inserção de Bytes": return auxiliar.desenquadroInsercaoBytes(quadroEnquadrado);
        case "Inserção de Bits": return auxiliar.desenquadroInsercaoBits(quadroEnquadrado);
        case "Violação da Camada Física": return auxiliar.desenquadroViolacaoFisica(quadroEnquadrado);
        default: return auxiliar.desenquadroContagemCaracteres(quadroEnquadrado);
    }
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