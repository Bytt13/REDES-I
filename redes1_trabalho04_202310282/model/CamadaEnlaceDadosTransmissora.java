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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
//imports que vamos precisar

public class CamadaEnlaceDadosTransmissora {
  FuncoesAuxiliares auxiliar = new FuncoesAuxiliares(); // Cria um objeto da nossa classe auxiliar para termos rapidez no codigo
  public static final Semaphore ackSemaphore = new Semaphore(0); // Cria o semaforo para esperar pelo ACK
  private static final int TIMEOUT_SEGUNDOS = 5; // Tempo do temporizador
  private TelaPrincipalController controller;

  // Define o tamanho de cada "pedaco" do quadro em bits para cada necessidade.
  private static final int TAMANHO_CHUNK = 32;
  private static final int TAMANHO_JANELA_GBN = 4;
  private static final int TAMANHO_JANELA_RS = 4;

  private static AtomicInteger ultimoAckRecebido = new AtomicInteger(-1);  // Variavel para guardar o numero do ultimo ACK recebido.
  private static Map<Integer, Timer> temporizadores = new ConcurrentHashMap<>(); // temporizadores para retransmissao seletiva
  private static Map<Integer, int[]> quadrosEnviados = new ConcurrentHashMap<>(); // quadros enviados para retransmissao seletiva

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
    temporizadores.values().forEach(Timer::cancel);
    temporizadores.clear();
    quadrosEnviados.clear();

    // 1. Dividir a mensagem original em varios pedacos (chunks)
    List<int[]> chunks = new ArrayList<>();
    for (int i = 0; i < bits.length; i += TAMANHO_CHUNK) {
        chunks.add(Arrays.copyOfRange(bits, i, Math.min(i + TAMANHO_CHUNK, bits.length)));
    }

    // 2. Escolher o protocolo de fluxo
    switch(fluxo)
    {
      case "Deslizante Go-Back-N":
        protocoloGoBackN(chunks, codificacao, enquadramento);
        break;
      case "Retransmissão Seletiva":
        protocoloRetransmissaoSeletiva(chunks, codificacao, enquadramento);
        break;
      case "Deslizante 1 bit":
        // Este caso tem o mesmo comportamento do default.
      default:
        // Este bloco agora executa para "Deslizante 1 bit" e para todos os outros casos não listados.
        for (int i = 0; i < chunks.size(); i++)
        {
          protocoloUmBit(chunks.get(i), i, codificacao, enquadramento);
        }
        break;
    } // Fim do switch
  }
  /**************************************************************
  * Metodo: protocoloUmBit
  * Funcao: aplica as lógicas de controle de erro, enquadramento e fluxo a um único quadro e o envia.
  * @param int[] quadro | um pedaço da mensagem original
  * @param int numeroSequencia | O numero de sequencia deste quadro
  * @param String codificacao | codificacao escolhida
  * @param String enquadramento | enquadramento escolhido
  * @return void
  * ********************************************************* */
  private void protocoloUmBit(int[] quadro, int numeroSequencia, String codificacao, String enquadramento) {
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
* Metodo: protocoloRetransmissaoSeletiva
* Funcao: Metodo para realizar a retransmissao seletiva
* @param List<int[]> chunks | quadr de bits recebido
* @param String codificacao | codificacao escolhida
* @param String enquadramento | enquadramento escolhido
* @return void
* ********************************************************* */
  private void protocoloRetransmissaoSeletiva(List<int[]> chunks, String codificacao, String enquadramento) {
    int base = 0;
    int proximoNumSequencia = 0;
    CamadaFisicaTransmissora fisicaTx = new CamadaFisicaTransmissora();

    // Lista para marcar quais ACKs recebemos.
    List<Integer> acksRecebidos = new ArrayList<>();

    while (base < chunks.size()) 
    {
        // Envia quadros enquanto a janela permitir
        while (proximoNumSequencia < base + TAMANHO_JANELA_RS && proximoNumSequencia < chunks.size()) {
            int[] chunk = chunks.get(proximoNumSequencia);
            int[] quadroComSequencia = auxiliar.adicionarNumeroDeSequencia(chunk, proximoNumSequencia);
            int[] quadroComControleErro = controleErro(quadroComSequencia);
            int[] quadroFinal = enquadramento(quadroComControleErro, enquadramento);

            quadrosEnviados.put(proximoNumSequencia, quadroFinal); // Guarda para possivel reenvio
            controller.setTextFieldBits(auxiliar.arrayToString(quadroFinal));
            fisicaTx.transmitir(quadroFinal, codificacao, this.controller);
            iniciarTemporizador(proximoNumSequencia, fisicaTx, codificacao); // Inicia um timer para ESTE quadro
            proximoNumSequencia++;
        }

        // Espera por um ACK. O semaforo aqui serve apenas para pausar a thread.
        try {
            if (ackSemaphore.tryAcquire(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)) {
                int ackNum = ultimoAckRecebido.get();
                if (!acksRecebidos.contains(ackNum)) {
                    acksRecebidos.add(ackNum);
                }

                // Para o temporizador do quadro que foi confirmado
                pararTemporizador(ackNum);

                // Se o ACK recebido for o da base da janela, desliza a janela
                if (ackNum == base) {
                    while (acksRecebidos.contains(base)) {
                        acksRecebidos.remove(Integer.valueOf(base));
                        quadrosEnviados.remove(base);
                        base++;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
}
/**************************************************************
* Metodo: iniciarTemporizador
* Funcao: inicia o temporizador
* @param int[] numSeq | numero recebido
* @param CamadaFisicaTransmissora fisicaTx | proxima camada
* @param String codificacao | codificacao escolhida
* @return void
* ********************************************************* */
private void iniciarTemporizador(int numSeq, CamadaFisicaTransmissora fisicaTx, String codificacao) {
  pararTemporizador(numSeq); // Cancela qualquer timer antigo para este numero
  Timer timer = new Timer();
  timer.schedule(new TimerTask() {
      @Override
      public void run() {
          // TIMEOUT! Reenvia apenas o quadro que se perdeu
          int[] quadroParaReenviar = quadrosEnviados.get(numSeq);
          if (quadroParaReenviar != null) {
              fisicaTx.transmitir(quadroParaReenviar, codificacao, controller);
              iniciarTemporizador(numSeq, fisicaTx, codificacao); // Reinicia o timer
          }
      }
  }, TIMEOUT_SEGUNDOS * 1000);
  temporizadores.put(numSeq, timer);
}
/**************************************************************
* Metodo: pararTemporizador
* Funcao: parar o temporizador ativo
* @param int[] numSeq | numero recebido
* @return void
* ********************************************************* */
private void pararTemporizador(int numSeq) {
  if (temporizadores.containsKey(numSeq)) {
      temporizadores.get(numSeq).cancel();
      temporizadores.remove(numSeq);
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