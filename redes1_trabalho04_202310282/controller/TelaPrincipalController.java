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
import model.CamadaEnlaceDadosReceptora;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
// imports que vamos precisar

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

  @FXML
  private ComboBox<String> comboBoxEnquadramento; //Declaro a caixa de selecao para o usuario escolher entre os 4 metodos

  @FXML
  private ComboBox<String> comboBoxErro; //Declaro a caixa de selecao para o usuario escolher entre os 3 metodos

  @FXML
  private ComboBox<String> comboBoxControleErro; //Declaro a caixa de selecao para o usuario escolher entre os 4 metodos

  @FXML
  private ComboBox<String> comboBoxFluxo; // Declara a caixa de selecao para o usuario escolher entre os 3 metodos

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
    comboBoxEnquadramento.getItems().addAll("Contagem de Caracteres", "Inserção de Bytes", "Inserção de Bits", "Violação da Camada Física"); //adiciona os elementos ao combo box
    comboBoxEnquadramento.getSelectionModel().selectFirst(); // Deixa o primeiro item já selecionado
    comboBoxErro.getItems().addAll("0%", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"); //adiciona os elementos ao combo box
    comboBoxErro.getSelectionModel().selectFirst(); // Deixa o primeiro item já selecionado
    comboBoxControleErro.getItems().addAll("Paridade Par", "Paridade Impar", "CRC", "Código de Hamming"); //adiciona os elementos ao combo box
    comboBoxControleErro.getSelectionModel().selectFirst(); // Deixa o primeiro item já selecionado
    comboBoxFluxo.getItems().addAll("Deslizante 1 bit", "Deslizante Go-Back-N", "Retransmissão Seletiva"); //adiciona os elementos ao combo box
    comboBoxFluxo.getSelectionModel().selectFirst(); // Deixa o primeiro item já selecionado
  } // Fim do metodo
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

    CamadaEnlaceDadosReceptora.reset();
    // Validacao para nao aceitar mensagens vazias
    if(mensagem == null || mensagem.trim().isEmpty())
    {
      // Verifica se a mensagem esta vazia, se estiver para o metodo
      return; //fim do metodo
    }
    textAreaMensagemFinal.clear();

    // Chamada da camada de aplicacao transmissora
    AplicacaoTransmissora appTx = new AplicacaoTransmissora();
    appTx.enviarMensagem(mensagem, codificacao, this);
  } // Fim do metodo

  /****************************************************************
  * Metodo: setTextFieldBits
  * Funcao: muda o texto do text field da mensagem em bits
  * @param String texto | o texto que vai aparecer na caixa de texto
  * @return void
  * ********************************************************* */
  public void setTextFieldBits(String texto)
  {
    Platform.runLater(() -> textFieldBits.setText(texto));
  } // Fim do metodo
  /****************************************************************
  * Metodo: setTextFieldCodificada
  * Funcao: muda o texto do text field da mensagem em bits codificada
  * @param String texto | o texto que vai aparecer na caixa de texto
  * @return void
  * ********************************************************* */
  public void setTextFieldCodificada(String texto)
  {
    Platform.runLater(() -> textFieldCodificada.setText(texto));
  } // Fim do metodo
  /****************************************************************
  * Metodo: setTextFieldSinal
  * Funcao: muda o texto do text field do sinal recebido
  * @param String texto | o texto que vai aparecer na caixa de texto
  * @return void
  * ********************************************************* */
  public void setTextFieldSinal(String texto)
  {
    Platform.runLater(() -> textFieldSinal.setText(texto));
  } // Fim do metodo
  /****************************************************************
  * Metodo: getTextFieldCodificada
  * Funcao: retorna o texto da caixa
  * @param void
  * @return String | texto que esta na caixa de texto codificada
  * ********************************************************* */
  public String getTextFieldCodificada()
  {
    return textFieldCodificada.getText();
  } // Fim do metodo
  /****************************************************************
  * Metodo: setTextFieldDecodificada
  * Funcao: muda o texto do text field da mensagem em bits decodificada
  * @param String texto | o texto que vai aparecer na caixa de texto
  * @return void
  * ********************************************************* */
  public void setTextFieldDecodificada(String texto)
  {
    Platform.runLater(() -> textFieldDecodificada.setText(texto));
  } // Fim do metodo
    /****************************************************************
  * Metodo: setTextAreaMensagemFinal
  * Funcao: Anexa o texto recebido ao text area da mensagem final
  * @param String texto | o texto que vai aparecer na caixa de texto
  * @return void
  * ********************************************************* */
  public void setTextAreaMensagemFinal(String texto)
  {
    Platform.runLater(() -> {
      textAreaMensagemFinal.appendText(texto);
    });
  } // Fim do metodo
    /****************************************************************
  * Metodo: getComboBoxCodificacao
  * Funcao: retorna o tipo de enquadramento
  * @param void
  * @return String | texto da comboBox
  * ********************************************************* */
    public String getComboBoxCodificacao()
  {
    return comboBoxCodificacao.getValue();
  } // Fim do metodo
  /****************************************************************
  * Metodo: getComboBoxEnquadramento
  * Funcao: retorna o tipo de enquadramento
  * @param void
  * @return String | texto da comboBox
  * ********************************************************* */
  public String getComboBoxEnquadramento()
  {
    return comboBoxEnquadramento.getValue();
  } // Fim do metodo
   /****************************************************************
  * Metodo: getComboBoxErro
  * Funcao: retorna a taxa de erro
  * @param void
  * @return String | texto da comboBox
  * ********************************************************* */
  public String getComboBoxErro()
  {
    return comboBoxErro.getValue();
  } // Fim do metodo
  /**************************************************************
* Metodo: exibirAlertaDeErro
* Funcao: Mostra um pop-up de alerta na tela informando sobre o erro.
* @param String mensagem | A mensagem a ser exibida no alerta.
* @return void
* ********************************************************* */
  public void emitirErro(String mensagem)
  {
    Platform.runLater(() -> {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("ERRO!!");
      alert.setHeaderText("Um erro foi detectado");
      alert.setContentText(mensagem);
      alert.showAndWait();
    });
  } // Fim do metodo

/* Metodo: getComboBoxControleErro
* Funcao: retorna o tipo de enquadramento
* @param void
* @return String | texto da comboBox
* ********************************************************* */
  public String getComboBoxControleErro()
  {
    return comboBoxControleErro.getValue();
  } // Fim do metodo

/****************************************************************
* Metodo: getComboBoxControleFluxo
* Funcao: retorna o tipo de controle de fluxo
* @param void
* @return String | texto da comboBox
* ********************************************************* */
public String getComboBoxControleFluxo()
{
  return comboBoxFluxo.getValue();
} // Fim do metodo

/**************************************************************
* Metodo: limparTextAreaMensagemFinal
* Funcao: Limpa o campo de texto da mensagem final.
* @param void
* @return void
* ********************************************************* */
  public void limparTextAreaMensagemFinal()
  {
    Platform.runLater(() -> {
      textAreaMensagemFinal.clear();
    });
  } // Fim do metodo
}