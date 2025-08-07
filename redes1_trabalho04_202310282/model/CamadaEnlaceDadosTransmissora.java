/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: CamadaEnlaceDadosTransmissora
* Funcao...........: Transfere a mensagem para camada fisica transmissora
*************************************************************** */
package model;

import controller.TelaPrincipalController;
import utils.FuncoesAuxiliares;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
//imports que vamos precisar
import java.util.concurrent.TimeUnit;

public class CamadaEnlaceDadosTransmissora {
  FuncoesAuxiliares auxiliar = new FuncoesAuxiliares(); // Cria um objeto da nossa classe auxiliar para termos rapidez no codigo
  public static final Semaphore acknack = new Semaphore(0); // Cria o semaforo para o ACK NACK
  private final ScheduledExecutorService temporizador = Executors.newScheduledThreadPool(1); // Cria o temporizador
  private ScheduledFuture<?> futureTimeout; // Cancelar alarme
  private static final int TIMEOUT_SEGUNDOS = 5; // Tempo do temporizador
  /**************************************************************
  * Metodo: transmitir
  * Funcao: envia a mensagem em bits para a proxima camada
  * @param int[] quadroDeBits | mensagem recebida (em bits)
  * @param String codificacao | codificacao escolhida
  * @param String enquadramento | enquadramento escolhido
  * @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
  * @return void 
  * ********************************************************* */
 public void transmitir(int[] bits, String codificacao, String enquadramento, TelaPrincipalController controller)
 {  
  String fluxo = controller.getComboBoxControleFluxo();
  int[] quadroControlado = controleFluxo(bits, codificacao, enquadramento, fluxo);
  // Chama a proxima caamda para realizar a codificacao e continuar a transmissaoß
  CamadaFisicaTransmissora fisicaTx = new CamadaFisicaTransmissora();
  fisicaTx.transmitir(quadroControlado, codificacao, controller);
  // inicia a logica try catch do temporizador
  try {
    futureTimeout = temporizador.schedule(() -> {
      System.out.println("TEMPORIZADOR: TIMEOUT! O ACK não chegou a tempo. Reenviando quadro...");
      transmitir(quadroControlado, codificacao, enquadramento, controller);
    },TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);

    System.out.println("ACK recebido!");
    acknack.acquire();
    futureTimeout.cancel(true);
  } catch(InterruptedException e) {
    futureTimeout.cancel(true);
    Thread.currentThread().interrupt();
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
  TelaPrincipalController controller = new TelaPrincipalController(); // Declara o controller para controlarmos a cena
  int[] quadroLimpo = controleErro(bits, controller); // aplica o controle de erro ao quadro de bits
  int[] quadroEnquadrado = enquadramento(quadroLimpo, enquadramento); // enquadra os bits
  int[] protocoloUmBit = quadroEnquadrado; //copia os bits enquadrados para enviar para proxima camada
  return protocoloUmBit;
}
}