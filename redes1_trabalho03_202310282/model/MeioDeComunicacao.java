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
    int[] fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBitsPontoA.length];
    // Simula o erro com probabilidade definida pela GUI
    try {
      String valorErro = erro.replace("%", "").trim();
      int chanceErro = Integer.parseInt(valorErro);
      // Converte a probabilidade da GUI num valor real
      Random rand = new Random();

      // Tenta simular um erro se a chance dele acontecer for maior que 0
      if(chanceErro > 0)
      {
        for(int i = 0; i < fluxoBrutoDeBitsPontoA.length; i++)
        {
          int numeroSorteado = rand.nextInt(100);
          if(numeroSorteado < chanceErro)
          {
            fluxoBrutoDeBitsPontoB[i] = 1 - fluxoBrutoDeBitsPontoA[i];
          }
          else
          {
            fluxoBrutoDeBitsPontoB[i] = fluxoBrutoDeBitsPontoA[i];
          }
        }
      }
      else
      {
        for(int i = 0; i < fluxoBrutoDeBitsPontoA.length; i++)
        {
          fluxoBrutoDeBitsPontoB[i] = fluxoBrutoDeBitsPontoA[i];
        }
      }
    } catch(NumberFormatException e) // Caso haja alguma excecao
    {
      e.printStackTrace();
    }
    
    StringBuilder bitsParaMostrar = new StringBuilder(); // Cria o string builder
    for(int bit : fluxoBrutoDeBitsPontoB)
    {
      bitsParaMostrar.append(bit);
    }

    controller.setTextFieldSinal(bitsParaMostrar.toString()); // Mostra a mensagem codificada no painel receptor
    System.out.println("O codigo saiu da camada meio de comunicacao");
    System.out.println(Arrays.toString(fluxoBrutoDeBitsPontoB));
    // Repassa a mensagem para proxima camada
    CamadaFisicaReceptora fisicaRx = new CamadaFisicaReceptora();
    fisicaRx.receber(fluxoBrutoDeBitsPontoB, codificacao, controller);
  }
}
