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
import java.util.Arrays;

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
    int[] quadroDeBitsVerificado = controleErro(quadroEnquadrado, controller);

    // Chama a proxima camada e passa os bits decodificados e desenquadrados
    if(quadroDeBitsVerificado != null)
    {
      int[] quadroDeBits = desenquadrar(quadroDeBitsVerificado, enquadramento, controller); // Desenquadra os bits

      //Debug
      System.out.println("Enlace receptor - OK");
      System.out.println(Arrays.toString(quadroDeBits));

      //Chama a proxima camada
      CamadaAplicacaoReceptora camadaAppRx = new CamadaAplicacaoReceptora();
      camadaAppRx.receber(quadroEnquadrado, controller);
    }
    else if(quadroDeBitsVerificado == null)
    {
      controller.setTextAreaMensagemFinal("");
      controller.emitirErro("Um erro foi detectado e descartado com sucesso");
      System.out.println("Deu null");
    }

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
    System.out.println("Desenquadrado");
    return quadroDesenquadrado;
  } // Fim do metodo

/**************************************************************
* Metodo: controleErro
* Funcao: Trata o erro com o valor escolhido
* @param int[] quadroDeBits | bits recebidos 
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return int[] quadroDeBitsVerificado | quadro de bits limpo de erros
* ********************************************************* */
  private int[] controleErro(int[] quadroDeBits, TelaPrincipalController controller)
  {
    int[] quadroDeBitsVerificado; // Cria um quadro de bits para armazenar ele verificado e tratado de possiveis erros
    String controleErro = controller.getComboBoxControleErro(); // Pega o tipo de controle de erro escolhido

    // Selecao para saber o metodo que vai usar dependendo do valor da comboBOx
    switch(controleErro)
    {
      case "Paridade Par":
        quadroDeBitsVerificado = auxiliar.paridadeParVerificacao(quadroDeBits, controller);
        break;
      case "Paridade Impar":
        quadroDeBitsVerificado = auxiliar.paridadeImparVerificacao(quadroDeBits, controller);
        break;
      case "CRC":
        quadroDeBitsVerificado = auxiliar.crcVerificacao(quadroDeBits, controller);
        break;
      case "Código de Hamming":
        quadroDeBitsVerificado = auxiliar.hammingVerificacao(quadroDeBits, controller);
        break;
      default:
        quadroDeBitsVerificado = auxiliar.paridadeParVerificacao(quadroDeBits, controller);
        break;
    } // Fim do switch
    System.out.println("Verificado");
    return quadroDeBitsVerificado;
  }// Fim do metodo
}