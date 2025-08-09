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
import java.util.concurrent.atomic.AtomicInteger;

public class CamadaEnlaceDadosTransmissora {
  FuncoesAuxiliares auxiliar = new FuncoesAuxiliares(); // Cria um objeto da nossa classe auxiliar para termos rapidez no codigo
  public static final Semaphore ackSemaphore = new Semaphore(0); // Cria o semaforo para esperar pelo ACK
  private static final int TIMEOUT_SEGUNDOS = 5; // Tempo do temporizador
  private TelaPrincipalController controller;

  // Define o tamanho de cada "pedaco" do quadro em bits.
  private static final int TAMANHO_CHUNK = 32;
  private static final int TAMANHO_JANELA_GBN = 4;

  // Variavel para guardar o numero do ultimo ACK recebido.
  private static AtomicInteger ultimoAckRecebido = new AtomicInteger(-1);

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
      String fluxo = controller.getComboBoxControleFluxo();

      // Limpa estado de transmissoes anteriores
      ultimoAckRecebido.set(-1);
      ackSemaphore.drainPermits();

      // 1. Dividir a mensagem original em varios pedacos (chunks)
      List<int[]> chunks = new ArrayList<>();
      for (int i = 0; i < bits.length; i += TAMANHO_CHUNK) {
          chunks.add(Arrays.copyOfRange(bits, i, Math.min(i + TAMANHO_CHUNK, bits.length)));
      }

      // 2. Escolher o protocolo de fluxo
      if ("Deslizante Go-Back-N".equals(fluxo)) {
          protocoloGoBackN(chunks, codificacao, enquadramento);
      } else { // "Deslizante 1 bit"
          for (int i = 0; i < chunks.size(); i++) {
              enviarQuadroStopAndWait(chunks.get(i), i, codificacao, enquadramento);
          }
      }
  }

  /**************************************************************
  * Metodo: enviarQuadroStopAndWait
  * Funcao: aplica as lógicas de controle de erro, enquadramento e fluxo a um único quadro e o envia.
  * @param int[] quadro | um pedaço da mensagem original
  * @param int numeroSequencia | O numero de sequencia deste quadro
  * @param String codificacao | codificacao escolhida
  * @param String enquadramento | enquadramento escolhido
  * @return void
  * ********************************************************* */
  private void enviarQuadroStopAndWait(int[] quadro, int numeroSequencia, String codificacao, String enquadramento) {
      int[] quadroComSequencia = auxiliar.adicionarNumeroDeSequencia(quadro, numeroSequencia);
      int[] quadroComControleErro = controleErro(quadroComSequencia);
      int[] quadroEnquadrado = enquadramento(quadroComControleErro, enquadramento);
      CamadaFisicaTransmissora fisicaTx = new CamadaFisicaTransmissora();

      while (true) {
          //System.out.println("S&W: Enviando quadro " + numeroSequencia);
          controller.setTextFieldBits(auxiliar.arrayToString(quadroEnquadrado));
          fisicaTx.transmitir(quadroEnquadrado, codificacao, this.controller);

          try {
              if (ackSemaphore.tryAcquire(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)) {
                  // Um ACK chegou, verifica se e o correto
                  if (ultimoAckRecebido.get() == numeroSequencia + 1) {
                      //System.out.println("S&W: ACK para quadro " + numeroSequencia + " recebido com sucesso!");
                      break; // Sucesso, pode enviar o proximo
                  }
              } else {
                  //System.out.println("S&W: TIMEOUT! Reenviando quadro " + numeroSequencia);
              }
          } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              break;
          }
      }
  }

  /**************************************************************
  * Metodo: protocoloGoBackN
  * Funcao: implementa a logica completa do Go-Back-N.
  * @param List<int[]> chunks | A lista de todos os pedacos da mensagem
  * @param String codificacao | codificacao escolhida
  * @param String enquadramento | enquadramento escolhido
  * @return void
  * ********************************************************* */
  private void protocoloGoBackN(List<int[]> chunks, String codificacao, String enquadramento) {
      int base = 0;
      int proximoNumeroSequencia = 0;
      CamadaFisicaTransmissora fisicaTx = new CamadaFisicaTransmissora();

      while (base < chunks.size()) {
          // Envia todos os quadros que cabem na janela
          while (proximoNumeroSequencia < base + TAMANHO_JANELA_GBN && proximoNumeroSequencia < chunks.size()) {
              int[] chunk = chunks.get(proximoNumeroSequencia);
              int[] quadroComSequencia = auxiliar.adicionarNumeroDeSequencia(chunk, proximoNumeroSequencia);
              int[] quadroComControleErro = controleErro(quadroComSequencia);
              int[] quadroFinal = enquadramento(quadroComControleErro, enquadramento);

              //System.out.println("GBN: Enviando quadro " + proximoNumeroSequencia);
              controller.setTextFieldBits(auxiliar.arrayToString(quadroFinal));
              fisicaTx.transmitir(quadroFinal, codificacao, this.controller);
              proximoNumeroSequencia++;
          }

          // Espera por um ACK ou timeout
          try {
              if (ackSemaphore.tryAcquire(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)) {
                  // Um ACK chegou, o GBN usa ACK cumulativo.
                  // O numero do ACK (N) confirma todos os quadros ate N-1.
                  //System.out.println("GBN: ACK " + ultimoAckRecebido.get() + " recebido. Base atual = " + base);
                  base = ultimoAckRecebido.get(); // A base avanca para o proximo numero esperado pelo receptor
                  //System.out.println("GBN: Janela desliza para base = " + base);
              } else {
                  // Timeout: reenvia todos os quadros da janela, a partir da base.
                  //System.out.println("GBN: TIMEOUT! Reenviando toda a janela a partir de " + base);
                  proximoNumeroSequencia = base; // Volta o ponteiro para reenviar a partir da base
              }
          } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              break;
          }
      }
  }

  /**************************************************************
  * Metodo: receberAck
  * Funcao: Metodo estatico para ser chamado pelo MeioDeComunicacao quando um ACK chega.
  * @param int[] ackFrame | O quadro de ACK recebido.
  * @return void
  * ********************************************************* */
  public static void receberAck(int[] ackFrame) {
      FuncoesAuxiliares aux = new FuncoesAuxiliares();
      int numAck = aux.extrairNumeroDoAck(ackFrame);
      if (numAck != -1) {
          //System.out.println("TRANSMISSOR: Recebido ACK explícito confirmando até o quadro " + (numAck - 1));
          ultimoAckRecebido.set(numAck);
          ackSemaphore.release(); // Libera o semaforo para o transmissor que esta esperando
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
    if(auxiliar.isQuadroAck(bits)) return bits; // Nao enquadra ACKs
    switch (enquadramento) {
        case "Contagem de Caracteres": return auxiliar.contagemCaracteres(bits);
        case "Inserção de Bytes": return auxiliar.insercaoBytes(bits);
        case "Inserção de Bits": return auxiliar.insercaoBits(bits);
        case "Violação da Camada Física": return auxiliar.violacaoFisica(bits);
        default: return auxiliar.contagemCaracteres(bits);
    }
  }

  /**************************************************************
* Metodo: controleErro
* Funcao: Trata o erro com o valor escolhido
* @param int[] quadroDeBits | bits recebidos
* @return int[] quadroDeBitsVerificado | quadro de bits limpo de erros
* ********************************************************* */
  private int[] controleErro(int[] quadroDeBits)
  {
    String controleErro = controller.getComboBoxControleErro(); // Pega o tipo de controle de erro escolhido
    switch (controleErro)
    {
      case "Paridade Par": return auxiliar.paridadePar(quadroDeBits);
      case "Paridade Impar": return auxiliar.paridadeImpar(quadroDeBits);
      case "CRC": return auxiliar.crc(quadroDeBits);
      case "Código de Hamming": return auxiliar.hamming(quadroDeBits);
      default: return auxiliar.paridadePar(quadroDeBits);
    }
  }
}