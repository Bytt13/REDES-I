/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: CamadaFisicaReceptora
* Funcao...........: Transfere a mensagem decodificada para camada aplicacao receptora
*************************************************************** */
package model;

import controller.TelaPrincipalController;
import utils.FuncoesAuxiliares;
//imports que precisamos
public class CamadaFisicaReceptora {
/**************************************************************
* Metodo: receber
* Funcao: decodifica os bits e passa eles para camada seguinte
* @param int[] bits | bits recebidos
* @param String codificacao | codificacao escolhida
* @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
* @return void 
* ********************************************************* */
  public void receber(int[] bits, String codificacao, TelaPrincipalController controller)
  {
    int[] fluxoDeBits; // Os bits originais, decodificados
    FuncoesAuxiliares auxiliar = new FuncoesAuxiliares(); // Cria o objeto para sermos capazes de utilizar as funcoes auxiliares

    // Estrutura para selecionar como decodificar os bits
    switch(codificacao)
    {
      case "Binario":
        fluxoDeBits = auxiliar.decodificacaoBinaria(bits);
        break;
      case "Manchester":
        fluxoDeBits = auxiliar.decodificacaoManchester(bits);
        break;
      case "Manchester Diferencial":
        fluxoDeBits = auxiliar.decodificacaoDiferencial(bits);
        break;
      default:
        fluxoDeBits = auxiliar.decodificacaoBinaria(bits);
        break;
    } // Fim do Switch

    String sequenciaDeBits = auxiliar.arrayToString(fluxoDeBits); // Transforma os bits em uma string para ser mostrada na GUI
    controller.setTextFieldDecodificada(sequenciaDeBits); // Mostra os bits na GUI

    // Chama a proxima camada
    CamadaEnlaceDadosReceptora enlaceRx = new CamadaEnlaceDadosReceptora();
    enlaceRx.receber(fluxoDeBits, controller);
  } // Fim do Metodo
} // Fim da classe
