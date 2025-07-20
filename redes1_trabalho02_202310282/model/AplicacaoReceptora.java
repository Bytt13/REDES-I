/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: AplicacaoReceptora
* Funcao...........: exibe a mensagem na GUI, ja decodificada e transformada em texto
*************************************************************** */
package model;

import controller.TelaPrincipalController;

public class AplicacaoReceptora {
  /**************************************************************
  * Metodo: exibirMensagem
  * Funcao: exibe a mensagem ja em texto na GUI
  * @param String mensagem | mensagem recebidos
  * @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
  * @return void 
  * ********************************************************* */
  public void exibirMensagem(String mensagem, TelaPrincipalController controller)
  {
    controller.setTextAreaMensagemFinal(mensagem); // Mostra a mensagem na caixa de texto
  }
}
