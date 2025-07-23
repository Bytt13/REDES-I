/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: CamadaEnlaceDadosReceptora
* Funcao...........: Transfere a mensagem codificada do meio de comunicacao para camadafisica receptora
*************************************************************** */
package model;

import controller.TelaPrincipalController;
import utils.FuncoesAuxiliares;

public class CamadaEnlaceDadosReceptora {
  FuncoesAuxiliares auxiliar = new FuncoesAuxiliares(); // Cria objeto de funcoes auxiliares para termos rapidez na hora de programar
/**************************************************************
* Metodo: receber
* Funcao: recebe os bits e passa eles para camada seguinte já desenquadrados
* @param int[] quadroEnquadrado | bits recebidos
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return void 
* ********************************************************* */
  public void receber(int[] quadroEnquadrado, TelaPrincipalController controller)
  {
    String enquadramento = controller.getComboBoxEnquadramento(); // Pega o tipo de enquadramento selecionado
    int[] quadroDeBits = desenquadrar(quadroEnquadrado, enquadramento, controller); // Desenquadra os bits

    // Chama a proxima camada e passa os bits decodificados e desenquadrados
    CamadaAplicacaoReceptora camadaAppRx = new CamadaAplicacaoReceptora();
    camadaAppRx.receber(quadroDeBits, controller);
  } // Fim do metodo

/**************************************************************
* Metodo: desenquadrar
* Funcao: desenquadra os bits e passa eles para camada seguinte
* @param int[] quadroEnquadrado | bits recebidos enquadrados
* @param String enquadramento | enquadramento escolhida
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return void 
* ********************************************************* */
  private int[] desenquadrar(int[] quadroEnquadrado, String enquadramento, TelaPrincipalController controller)
  {
    int[] quadroDesenquadrado; // cria o array que vai armazenar o quadro de bits já desenquadrado

    // Switch para escolehr o tipo de enquadramento
    switch(enquadramento)
    {
      case "Contagem de Caracteres":
        quadroDesenquadrado = auxiliar.desenquadroContagemCaracteres(quadroEnquadrado);
        break;
      case "Inserção de Bytes":
        quadroDesenquadrado = auxiliar.desenquadroInsercaoBytes(quadroEnquadrado);
        break;
      case "Inserção de Bits":
        quadroDesenquadrado = auxiliar.desenquadroInsercaoBits(quadroEnquadrado);
        break;
      case "Violação da Camada Física":
        quadroDesenquadrado = auxiliar.desenquadroViolacaoFisica(quadroEnquadrado);
        break;
      default: 
        quadroDesenquadrado = auxiliar.desenquadroContagemCaracteres(quadroEnquadrado);
        break;
    } // Fim do switch
    return quadroDesenquadrado;
  } // Fim do metodo
}