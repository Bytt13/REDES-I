/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: MeioDeComunicacao
* Funcao...........: Transfere a mensagem codificada da camada transmissora para receptora
*************************************************************** */
package model;

import controller.TelaPrincipalController;
//import que vamos precisar

public class MeioDeComunicacao {
/**************************************************************
* Metodo: transferir
* Funcao: transfere a mensagem em forma de bits codificados para a proxima camada
* @param int[] fluxoBrutoDeBits | fluxo de bits recebido
* @param String codificacao | codificacao escolhida
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return void 
* ********************************************************* */
  public void transferir(int[] fluxoBrutoDeBitsPontoA, String codificacao, TelaPrincipalController controller)
  {
    controller.setTextFieldSinal(controller.getTextFieldCodificada()); // Mostra a mensagem codificada no painel receptor

    // Repassa a mensagem para proxima camada
    CamadaFisicaReceptora fisicaRx = new CamadaFisicaReceptora();
    fisicaRx.receber(fluxoBrutoDeBitsPontoA, codificacao, controller);
  }
}
