/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: CamadaFisicaReceptora
* Funcao...........: Transfere a mensagem decodificada para camada aplicacao receptora
*************************************************************** */
package model;

import java.util.ArrayList;
import java.util.Arrays;

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
    
    // if para sanitizar o manchester e manchester diferencial para controle de fluxo de retransmissao seletiva
    //String enquadramento = controller.getComboBoxEnquadramento();
    /*if (("Manchester".equals(codificacao) || "Manchester Diferencial".equals(codificacao))
        && !"Violação da Camada Física".equals(enquadramento)) {
      fluxoDeBits = sanitizarFluxo(fluxoDeBits);
    }*/

    String sequenciaDeBits = auxiliar.arrayToString(fluxoDeBits); // Transforma os bits em uma string para ser mostrada na GUI
    controller.setTextFieldDecodificada(sequenciaDeBits); // Mostra os bits na GUI
    System.out.println("Codigo foi decodificado");
    System.out.println(Arrays.toString(fluxoDeBits));
    // Chama a proxima camada
    CamadaEnlaceDadosReceptora enlaceRx = new CamadaEnlaceDadosReceptora();
    enlaceRx.receber(fluxoDeBits, controller);
  } // Fim do Metodo

  /**************************************************************
    * Metodo: sanitizarFluxo
    * Funcao: Remove todos os valores que não sejam 0 ou 1 do fluxo de bits.
    * @param int[] fluxo | O fluxo de bits decodificado.
    * @return int[] | O fluxo de bits contendo apenas 0s e 1s.
    * ********************************************************* */
    /*private int[] sanitizarFluxo(int[] fluxo) {
        if (fluxo == null) return new int[0];
        
        ArrayList<Integer> bitsLimpados = new ArrayList<>();
        for (int bit : fluxo) {
            if (bit == 0 || bit == 1) {
                bitsLimpados.add(bit);
            }
        }
        
        // Converte o ArrayList de volta para um array de int
        int[] resultado = new int[bitsLimpados.size()];
        for (int i = 0; i < bitsLimpados.size(); i++) {
            resultado[i] = bitsLimpados.get(i);
        }
        return resultado;
    } // FIm do metodo*/
} // Fim da classe
