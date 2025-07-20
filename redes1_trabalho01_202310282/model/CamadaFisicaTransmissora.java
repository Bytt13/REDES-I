/***************************************************************** 
* Autor............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: CamadaFisicaTransmissora
* Funcao...........: Codifica os bits da mensagem recebida
*************************************************************** */
package model;

import controller.TelaPrincipalController;
import utils.FuncoesAuxiliares;
//imports que precisaremos

public class CamadaFisicaTransmissora {
  /**************************************************************
  * Metodo: transmitir
  * Funcao: envia a mensagem (em bits) codificada para a proxima camada
  * @param int[] quadroDeBits | mensagem recebida (em bits)
  * @param String codificacao | codificacao escolhida
  * @param TelaPrincipalController controller | controller para conseguirmos gerenciar a tela
  * @return void 
  * ********************************************************* */
  public void transmitir(int[] bits, String codificacao, TelaPrincipalController controller)
  {
    int[] fluxoBrutoDeBits; // Cria o Array para o fluxo bruto de bits
    FuncoesAuxiliares auxiliar = new FuncoesAuxiliares(); // Cria o objeto para sermos capazes de utilizar as funcoes auxiliares

    // Escolha do tipo codificacao de acordo com a comboBox, deixando o binario, como opcao padrao, que no controller ja esta pre selecionado
    switch (codificacao) {
      case "Binario":
        fluxoBrutoDeBits = auxiliar.codificacaoBinaria(bits);
        break;
      case "Manchester":
        fluxoBrutoDeBits = auxiliar.codificacaoManchester(bits);
        break;
      case "Manchester Diferencial":
        fluxoBrutoDeBits = auxiliar.codificacaoManchesterDiferencial(bits);
        break;
      default:
        fluxoBrutoDeBits = auxiliar.codificacaoBinaria(bits);
        break;
    } // Fim do Switch

    controller.setTextFieldCodificada(auxiliar.arrayToString(fluxoBrutoDeBits)); // Mostra a mensagem em bits, codificada na caixa de texto

    // Chamada do meio de comunicacao para enviar os dados
    MeioDeComunicacao meio = new MeioDeComunicacao();
    meio.transferir(fluxoBrutoDeBits, codificacao, controller);
  } // Fim do metodo
} // Fim da classe