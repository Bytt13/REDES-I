/***************************************************************** 
* Autor............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: CamadaAplicacaoTransmissora
* Funcao...........: Transforma a mensagem recebida em bits, nao bytes
*************************************************************** */
package model;

import controller.TelaPrincipalController;

public class CamadaAplicacaoTransmissora {
/**************************************************************
* Metodo: transmitir
* Funcao: envia a mensagem em forma de bits para a proxima camada
* @param String mensagem | mensagem recebida
* @param String codificacao | codificacao escolhida
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return void 
* ********************************************************* */
  public void transmitir(String mensagem, String codificacao, TelaPrincipalController controller)
  {
    int[] quadroDeBits = new int[mensagem.length() * 8];
    // Cria o builder para criar a string
    StringBuilder bitsString = new StringBuilder(); 
    
    //Loop para transformar as letras em bits, e os bits em string para serem mostrados nas caixas de texto
    for(int i = 0; i < mensagem.length(); i++)
    {
      char caractere = mensagem.charAt(i);
      String binario = String.format("%8s", Integer.toBinaryString(caractere)).replace(' ', '0');
      bitsString.append(binario).append(" ");
      
      for(int j = 0; j < 8; j++)
      {
        int indice = i * 8 + j;
        quadroDeBits[indice] = Character.getNumericValue(binario.charAt(j));
      }
    }
    String enquadramento = controller.getComboBoxEnquadramento(); //Pega o tipo de enquadramento selecionado

    // Chama a proxima camada, e transmite a mensagem em bits para ela
    CamadaEnlaceDadosTransmissora enlaceTx = new CamadaEnlaceDadosTransmissora();
    enlaceTx.transmitir(quadroDeBits, codificacao, enquadramento, controller);

  }
}
