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
//imports que vamos precisar

public class CamadaEnlaceDadosTransmissora {
  FuncoesAuxiliares auxiliar = new FuncoesAuxiliares(); // Cria um objeto da nossa classe auxiliar para termos rapidez no codigo
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
    int[] quadroEnquadrado = enquadramento(bits, enquadramento); // Chama a funcao de enquadramento e passa o quadro de bits enquadrados para proxima camada

    controller.setTextFieldBits(auxiliar.arrayToString(quadroEnquadrado)); //mostra os bits enquadrados na GUI

    // Chama a proxima caamda para realizar a codificacao e continuar a transmissao
    CamadaFisicaTransmissora fisicaTx = new CamadaFisicaTransmissora();
    fisicaTx.transmitir(quadroEnquadrado, codificacao, controller);
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
    }

    return quadroEnquadrado;
  }
}