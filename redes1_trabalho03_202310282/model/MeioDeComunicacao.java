/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: MeioDeComunicacao
* Funcao...........: Transfere a mensagem codificada da camada transmissora para receptora
*************************************************************** */
package model;

import java.util.Random;
import controller.TelaPrincipalController;
//import que vamos precisar

public class MeioDeComunicacao {
/**************************************************************
* Metodo: transferir
* Funcao: transfere a mensagem em forma de bits codificados para a proxima camada, e aplica um erro caso ocorra
* @param int[] fluxoBrutoDeBits | fluxo de bits recebido
* @param String codificacao | codificacao escolhida
* @param String erro | taxa de erro escolhida
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return void 
* ********************************************************* */
  public void transferir(int[] fluxoBrutoDeBitsPontoA, String codificacao, String erro, TelaPrincipalController controller)
  { 
    // Simula o erro com probabilidade definida pela GUI
    try {
      String valorErro = erro.replace("%", "").trim();
      int chanceErro = Integer.parseInt(valorErro);
      // Converte a probabilidade da GUI num valor real

      // Tenta simular um erro se a chance dele acontecer for maior que 0
      if(chanceErro > 0)
      {
        Random rand = new Random();
        int numeroSorteado = rand.nextInt(100); // Sorteia um numero de 0 a 100

        if(numeroSorteado < chanceErro)
        {
          int indiceErro = rand.nextInt(fluxoBrutoDeBitsPontoA.length); // Gera uma posicao aleatoria para o bit que vai ser alterado
          fluxoBrutoDeBitsPontoA[indiceErro] = 1 - fluxoBrutoDeBitsPontoA[indiceErro]; // Inverte o bit
          controller.emitirErro("Ocorreu um erro!");
        }
      }
    } catch(NumberFormatException e) // Caso haja alguma excecao
    {
      e.printStackTrace();
    }
    controller.setTextFieldSinal(controller.getTextFieldCodificada()); // Mostra a mensagem codificada no painel receptor

    // Repassa a mensagem para proxima camada
    CamadaFisicaReceptora fisicaRx = new CamadaFisicaReceptora();
    fisicaRx.receber(fluxoBrutoDeBitsPontoA, codificacao, controller);
  }
}
