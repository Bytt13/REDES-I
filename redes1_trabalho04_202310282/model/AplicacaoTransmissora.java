/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: AplicacaoTransmissora
* Funcao...........: Envia a mensagem captada pelo controller para a proxima camada da aplicacao
*************************************************************** */
package model;

import controller.TelaPrincipalController;
//import das classes que vamos precisar utilizar

public class AplicacaoTransmissora {
  /**************************************************************
* Metodo: enviarMensagem
* Funcao: envia a mensagem em forma de string para a proxima camada
* @param String mensagem | mensagem recebida
* @param String codificacao | codificacao escolhida
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return void 
 * ********************************************************* */
  public void enviarMensagem(String mensagem, String codificacao, TelaPrincipalController controller){
    controller.setTextFieldBits("Enviando mensagem..."); // Mostra rapidamente que a mensagem esta sendo enviada 
    System.out.println("Enviando mensagem");
    // Chama a proxima camada
    CamadaAplicacaoTransmissora camadaAppTx = new CamadaAplicacaoTransmissora();
    camadaAppTx.transmitir(mensagem, codificacao, controller);
  }
}
