/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: CamadaAplicacaoReceptora
* Funcao...........: Transfere a mensagem decodificada para camada aplicacao receptora
*************************************************************** */
package model;

import controller.TelaPrincipalController;
//imports que precisamos

public class CamadaAplicacaoReceptora {
  /**************************************************************
  * Metodo: receber
  * Funcao: recebe os bits e passa eles para camada seguinte
  * @param int[] bits | bits recebidos
  * @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
  * @return void 
  * ********************************************************* */
  public void receber(int[] bits, TelaPrincipalController controller)
  {
    String mensagem; // Mensagem final
    String binario; // Sequencia de bits em binario
    StringBuilder builderMensagem = new StringBuilder(); // Cria um builder para podermos construir a mensagem em si
    //Loop que constroi a mensagem, e separa os bits em blocos de 8
    for(int i = 0; i < bits.length / 8; i++)
    {
      StringBuilder builderBinario = new StringBuilder(); // Cria o builder de string da sequencia binaria
      for(int j = 0; j < 8; j++)
      {
        builderBinario.append(bits[i * 8 + j]); // Constroi a string de binarios para ser convertida em texto
      }
      binario  = builderBinario.toString();
      // Converte o texto binario para um inteiro e depois para um caractere
      int valorAscii = Integer.parseInt(binario, 2);
      builderMensagem.append((char) valorAscii);
    }
    mensagem = builderMensagem.toString(); // Escreve de fato a mensagem
    System.out.println(mensagem);
    // Chama a ultima camada e passa a mensagem para ela
    AplicacaoReceptora appRx = new AplicacaoReceptora();
    appRx.exibirMensagem(mensagem, controller);
  }
}
