/*****************************************************************
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........:
* Ultima alteracao.:
* Nome.............: MeioDeComunicacao
* Funcao...........: Transfere a mensagem codificada da camada transmissora para receptora
*************************************************************** */
package model;

import java.util.Arrays;
import java.util.Random;
import controller.TelaPrincipalController;
import utils.FuncoesAuxiliares;

public class MeioDeComunicacao {
  private FuncoesAuxiliares auxiliar = new FuncoesAuxiliares();

/**************************************************************
* Metodo: transferir
* Funcao: transfere a mensagem em forma de bits codificados para a proxima camada, e aplica um erro caso ocorra
* @param int[] fluxoBrutoDeBitsPontoA | fluxo de bits recebido
* @param String codificacao | codificacao escolhida
* @param String erro | taxa de erro escolhida
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return void
* ********************************************************* */
  public void transferir(int[] fluxoBrutoDeBitsPontoA, String codificacao, String erro, TelaPrincipalController controller)
  {
    // 1. Verifica se o quadro e um ACK
    if (auxiliar.isQuadroAck(fluxoBrutoDeBitsPontoA)) {
        System.out.println("MEIO: Quadro de ACK detectado. Enviando de volta para o Transmissor...");
        // Em vez de ir para o receptor, o ACK volta para a camada de enlace transmissora.
        // ACKs sao enviados sem erro para simplificar a simulacao.
        CamadaEnlaceDadosTransmissora.receberAck(fluxoBrutoDeBitsPontoA);
        return; // A transferencia do ACK termina aqui.
    }

    // 2. Logica original para transferencia de DADOS (com possivel erro)
    // ... (dentro do método transferir)
    int[] fluxoBrutoDeBitsPontoB;
    Random rand = new Random();

    try {
      String valorErro = erro.replace("%", "").trim();
      int chanceErro = Integer.parseInt(valorErro);

      // Comecamos com uma copia perfeita do quadro original.
      fluxoBrutoDeBitsPontoB = Arrays.copyOf(fluxoBrutoDeBitsPontoA, fluxoBrutoDeBitsPontoA.length);

      // Sorteamos UMA VEZ para ver se o quadro tera erro.
      if (rand.nextInt(100) < chanceErro) {
        System.out.println("MEIO: Um erro foi introduzido no quadro!");
        // Se o quadro foi "sorteado" para ter erro, invertemos um bit aleatorio.
        if (fluxoBrutoDeBitsPontoA.length > 0) {
          int bitParaInverter = rand.nextInt(fluxoBrutoDeBitsPontoA.length);
          fluxoBrutoDeBitsPontoB[bitParaInverter] = 1 - fluxoBrutoDeBitsPontoA[bitParaInverter];
        }
      }
    } catch(NumberFormatException e) {
      e.printStackTrace();
      fluxoBrutoDeBitsPontoB = Arrays.copyOf(fluxoBrutoDeBitsPontoA, fluxoBrutoDeBitsPontoA.length);
    }

    // 3. Exibe e envia o quadro de DADOS para o receptor
    StringBuilder bitsParaMostrar = new StringBuilder();
    for(int bit : fluxoBrutoDeBitsPontoB) {
      bitsParaMostrar.append(bit);
    }

    controller.setTextFieldSinal(bitsParaMostrar.toString());
    System.out.println("MEIO: Quadro de DADOS atravessou o meio de comunicacao");
    System.out.println(Arrays.toString(fluxoBrutoDeBitsPontoB));

    CamadaFisicaReceptora fisicaRx = new CamadaFisicaReceptora();
    fisicaRx.receber(fluxoBrutoDeBitsPontoB, codificacao, controller);
  }
}