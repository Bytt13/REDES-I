/***************************************************************** 
* Autor............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: TelaPrincipalController
* Funcao...........: Faz a mediacao entre codigo e GUI, controlando o que deve ser feito quando acontecer alguma acao na interface
*************************************************************** */
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.AplicacaoTransmissora;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;

public class TelaPrincipalController {

  @FXML
  private Button buttonSimular; //Declaro o botao de simular a transmissao

  @FXML
  private TextField textFieldBits; //Declaro o campo de texto da mensagem em bits

  @FXML
  private TextField textFieldCodificada; //Declaro o campo de texto da mensagem codificada

  @FXML
  private TextField textFieldSinal; //Declaro o campo de texto do sinal recebido

  @FXML
  private TextField textFieldDecodificada; //Declaro o campo de texto do sinal decodificado

  @FXML
  private TextArea textAreaMensagemOriginal; //Declaro o campo de texto da mensagem que o usuario digita

  @FXML
  private TextArea textAreaMensagemFinal; //Declaro o campo de texto da mensagem em sua forma final, depois de ser decodificada e transformada em textos

  @FXML
  private ComboBox<String> comboBoxCodificacao; //Declaro a caixa de selecao para o usuario escolher entre os 3 metodos

  /****************************************************************
  * Metodo: initialize
  * Funcao: carrega os elementos fxml para tela
  * @param void
  * @return void 
  * ********************************************************* */
  @FXML
  public void initialize()
  {
    comboBoxCodificacao.getItems().addAll("Binario", "Manchester", "Manchester Diferencial"); //adiciona os elementos ao combo box
    comboBoxCodificacao.getSelectionModel().selectFirst(); // Deixa o primeiro item já selecionado
    
  }
  /**************************************************************
  * Metodo: botao
  * Funcao: faz o botao iniciar a simulacao
  * @param void
  * @return void 
  * ********************************************************* */
  @FXML
  private void botao()
  {
    String mensagem = textAreaMensagemOriginal.getText(); // Guarda a mensagem numa variavel tipo string
    String codificacao = comboBoxCodificacao.getValue(); // Guarda a codificacao escolhida

    // Validacao para nao aceitar mensagens vazias
    if(mensagem == null || mensagem.trim().isEmpty())
    {
      // Verifica se a mensagem esta vazia, se estiver para o metodo
      return; //fim do metodo
    }
    // Chamada da camada de aplicacao transmissora
    AplicacaoTransmissora appTx = new AplicacaoTransmissora();
    appTx.enviarMensagem(mensagem, codificacao, this);
  }

  /****************************************************************
  * Metodo: setTextFieldBits
  * Funcao: muda o texto do text field da mensagem em bits
  * @param String texto | o texto que vai aparecer na caixa de texto
  * @return void 
  * ********************************************************* */
  public void setTextFieldBits(String texto)
  {
  textFieldBits.setText(texto);
  }
  /****************************************************************
  * Metodo: setTextFieldCodificada
  * Funcao: muda o texto do text field da mensagem em bits codificada
  * @param String texto | o texto que vai aparecer na caixa de texto
  * @return void 
  * ********************************************************* */
  public void setTextFieldCodificada(String texto)
  {
  textFieldCodificada.setText(texto);
  }
  /****************************************************************
  * Metodo: setTextFieldSinal
  * Funcao: muda o texto do text field do sinal recebido
  * @param String texto | o texto que vai aparecer na caixa de texto
  * @return void 
  * ********************************************************* */
  public void setTextFieldSinal(String texto)
  {
  textFieldSinal.setText(texto);
  }
  /****************************************************************
  * Metodo: getTextFieldCodificada
  * Funcao: muda o texto do text field da mensagem em bits codificada
  * @param void
  * @return String | texto que esta na caixa de texto codificada
  * ********************************************************* */
  public String getTextFieldCodificada()
  {
  return textFieldCodificada.getText();
  }
  /****************************************************************
  * Metodo: setTextFieldDecodificada
  * Funcao: muda o texto do text field da mensagem em bits decodificada
  * @param String texto | o texto que vai aparecer na caixa de texto
  * @return void 
  * ********************************************************* */
  public void setTextFieldDecodificada(String texto)
  {
  textFieldDecodificada.setText(texto);
  }
    /****************************************************************
  * Metodo: setTextAreaMensagemFinal
  * Funcao: muda o texto do text area da mensagem final
  * @param String texto | o texto que vai aparecer na caixa de texto
  * @return void 
  * ********************************************************* */
  public void setTextAreaMensagemFinal(String texto)
  {
  textAreaMensagemFinal.setText(texto);
  }
}
