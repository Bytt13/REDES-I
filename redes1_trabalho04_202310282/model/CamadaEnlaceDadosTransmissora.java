/*****************************************************************
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........:
* Ultima alteracao.:
* Nome.............: CamadaEnlaceDadosTransmissora
* Funcao...........: Transfere a mensagem para camada fisica transmissora em quadros
*************************************************************** */
package model;

import controller.TelaPrincipalController;
import utils.FuncoesAuxiliares;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
//imports que vamos precisar


public class CamadaEnlaceDadosTransmissora {
  FuncoesAuxiliares auxiliar = new FuncoesAuxiliares(); // Cria um objeto da nossa classe auxiliar para termos rapidez no codigo
  public static final Semaphore acknack = new Semaphore(0); // Cria o semaforo para o ACK NACK
  private static final int TIMEOUT_SEGUNDOS = 5; // Tempo do temporizador
  private TelaPrincipalController controller;

  // Define o tamanho de cada "pedaco" do quadro em bits.
  // Para fins de exemplo, vamos usar 32 bits (4 bytes) por quadro.
  // Em uma aplicacao real, esse valor pode ser negociado ou fixo pelo protocolo.
  private static final int TAMANHO_CHUNK = 32;

  /**************************************************************
  * Metodo: transmitir
  * Funcao: envia a mensagem em bits para a proxima camada, dividida em quadros
  * @param int[] bits | mensagem recebida (em bits)
  * @param String codificacao | codificacao escolhida
  * @param String enquadramento | enquadramento escolhido
  * @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
  * @return void
  * ********************************************************* */
  public void transmitir(int[] bits, String codificacao, String enquadramento, TelaPrincipalController controller) {
      this.controller = controller;

      // 1. Dividir a mensagem original em varios pedacos (chunks)
      List<int[]> chunks = new ArrayList<>();
      for (int i = 0; i < bits.length; i += TAMANHO_CHUNK) {
          int end = Math.min(i + TAMANHO_CHUNK, bits.length);
          chunks.add(Arrays.copyOfRange(bits, i, end));
      }

      // 2. Enviar cada chunk individualmente
      for (int[] chunk : chunks) {
          enviarQuadro(chunk, codificacao, enquadramento);
      }
  }

  /**************************************************************
  * Metodo: enviarQuadro
  * Funcao: aplica as lógicas de controle de erro, enquadramento e fluxo a um único quadro e o envia.
  * @param int[] quadro | um pedaço da mensagem original
  * @param String codificacao | codificacao escolhida
  * @param String enquadramento | enquadramento escolhido
  * @return void
  * ********************************************************* */
  private void enviarQuadro(int[] quadro, String codificacao, String enquadramento) {
      String fluxo = this.controller.getComboBoxControleFluxo();
      int[] quadroControlado = controleFluxo(quadro, codificacao, enquadramento, fluxo);
      CamadaFisicaTransmissora fisicaTx = new CamadaFisicaTransmissora();

      // Loop para continuar enviando ate que um ACK seja recebido para este quadro
      while (true) {
          System.out.println("Enviando quadro para a camada física...");
          controller.setTextFieldBits(auxiliar.arrayToString(quadroControlado));
          fisicaTx.transmitir(quadroControlado, codificacao, this.controller);

          try {
              // Tenta adquirir o semaforo (esperar pelo ACK) com um timeout.
              if (acknack.tryAcquire(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)) {
                  System.out.println("ACK recebido! Transmissão do quadro bem-sucedida.");
                  break; // ACK recebido, sai do loop e passa para o próximo quadro.
              } else {
                  System.out.println("TEMPORIZADOR: TIMEOUT! O ACK não chegou a tempo. Reenviando quadro...");
                  // O loop continuará e o quadro será reenviado.
              }
          } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              System.err.println("Thread de transmissão interrompida.");
              break; // Sai do loop se a thread for interrompida.
          }
      }
  }

   /**************************************************************
  * Metodo: enquadramento
  * Funcao: enquadra os bits
  * @param int[] quadroDeBits | mensagem recebida (em bits)
  * @param String enquadramento | enquadramento escolhida
  * @return int[] quadroEnquadrado | quadro de bits ja enquadrado
  * ********************************************************* */
  private int[] enquadramento(int[] bits, String enquadramento)
  {
    int[] quadroEnquadrado; // Cria o array de bits enquadrado

    // Switch para escolehr o tipo de enquadramento
    switch(enquadramento)
    {
      case "Contagem de Caracteres":
        quadroEnquadrado = auxiliar.contagemCaracteres(bits);
        break;
      case "Inserção de Bytes":
        quadroEnquadrado = auxiliar.insercaoBytes(bits);
        break;
      case "Inserção de Bits":
        quadroEnquadrado = auxiliar.insercaoBits(bits);
        break;
      case "Violação da Camada Física":
        quadroEnquadrado = auxiliar.violacaoFisica(bits);
        break;
      default:
        quadroEnquadrado = auxiliar.contagemCaracteres(bits);
        break;
    } // Fim do switch

    return quadroEnquadrado;
  } // Fim do metodo

  /**************************************************************
* Metodo: controleErro
* Funcao: Trata o erro com o valor escolhido
* @param int[] quadroDeBits | bits recebidos
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return int[] quadroDeBitsVerificado | quadro de bits limpo de erros
* ********************************************************* */
  private int[] controleErro(int[] quadroDeBits, TelaPrincipalController controller)
  {
    int[] quadroDeBitsControlado; // Cria um quadro de bits para armazenar ele verificado e tratado de possiveis erros
    String controleErro = controller.getComboBoxControleErro(); // Pega o tipo de controle de erro escolhido

    // Selecao para saber o metodo que vai usar dependendo do valor da comboBOx
    switch(controleErro)
    {
      case "Paridade Par":
        quadroDeBitsControlado = auxiliar.paridadePar(quadroDeBits);
        break;
      case "Paridade Impar":
        quadroDeBitsControlado = auxiliar.paridadeImpar(quadroDeBits);
        break;
      case "CRC":
        quadroDeBitsControlado = auxiliar.crc(quadroDeBits);
        break;
      case "Código de Hamming":
        quadroDeBitsControlado = auxiliar.hamming(quadroDeBits);
        break;
      default:
        quadroDeBitsControlado = auxiliar.paridadePar(quadroDeBits);
        break;
    } // Fim do switch
    System.out.println("o codigo saiu do controle de erro transmissor");
    return quadroDeBitsControlado;
  } // Fim do metodo

/**************************************************************
* Metodo: controleFluxo
* Funcao: controla o codigo
* @param int[] bits | bits recebidos
* @param String codificacao | tipo de codificacao
* @param String enquadramento | tipo de enquadramento escolhido
* @param String fluxo | tipo de fluxo escolhido
* @return int[] quadroControlado | quadro de bits controlado
* ********************************************************* */
private int[] controleFluxo(int[] bits, String codificacao, String enquadramento, String fluxo)
{
  int[] quadroControlado;
  switch(fluxo)
  {
    case "Deslizante 1 bit":
      quadroControlado = protocoloUmBit(bits, codificacao, enquadramento);
      break;
    default:
      quadroControlado = protocoloUmBit(bits, codificacao, enquadramento);
  }

  return quadroControlado;
}

/**************************************************************
* Metodo: protocoloUmBit
* Funcao: devolve os bits com janela deslizante 1 bit
* @param int[] bits | bits recebidos
* @param String codificacao | tipo de codificacao
* @param String enquadramento | tipo de enquadramento escolhido
* @return int[] protocoloUmBit | quadro de bits limpo de erros
* ********************************************************* */
private int[] protocoloUmBit(int[] bits, String codificacao, String enquadramento)
{
  int[] quadroLimpo = controleErro(bits, controller); // aplica o controle de erro ao quadro de bits
  int[] quadroEnquadrado = enquadramento(quadroLimpo, enquadramento); // enquadra os bits
  int[] protocoloUmBit = quadroEnquadrado; //copia os bits enquadrados para enviar para proxima camada
  return protocoloUmBit;
}
}