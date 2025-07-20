/* **************************************************************** 
* Autor............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: Principal
* Funcao...........: Roda o programa com o comando javac Principal.java
*************************************************************** */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.TelaPrincipalController;
//import das bibliotecas que vamos utilizar

public class Principal extends Application{
/****************************************************************
* Metodo: start
* Funcao: carrega os elementos fxml para tela
* @param Stage stage
* @throws Exception para casos de erros
* @return void 
 * ********************************************************* */
@Override
public void start(Stage stage) throws Exception
{
  //Stage é o "palco" que o FXML usa para apresentar o cenario
  FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaPrincipal.fxml")); //Cria o loader que carrega os dados do arquivo fxml
  Parent root = loader.load(); //Monta de fato o cenario
  Scene scene = new Scene(root); //Cria uma cena para o cenario, que armazena os dados dele
  stage.setTitle("Simulador de Erros e enquadramentos"); //Adiciona um titulo a janela
  stage.setScene(scene); //Coloca a cena criada para rodar dentro da janela
  stage.setResizable(false); //Nao deixa a janela ser redimensionavel
  stage.setOnCloseRequest(event -> {
    Platform.exit();
    System.exit(0); //Garante que o sistema feche por completo ao fechar a janela
  });
  stage.show(); //Faz a janela ser visivel
}
/****************************************************************
* Metodo: main
* Funcao: roda o programa java
* @param String[] args
* @return void 
 * ********************************************************* */
  public static void main(String[] args) {
    launch(args); //incializa o programa
  }
}
