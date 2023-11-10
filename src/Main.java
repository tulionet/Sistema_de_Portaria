import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class Main extends Application {

    private App.UserType tipo;
    User user;

    public Main(User user) {
        this.user = user;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tela Principal");
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Label nameLabel = new Label("Bem-vindo, " + user.getNome() );
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button nfButton = new Button("Entrada Nota Fiscal");
        Button visitorButton = new Button("Entrada Visitante");

        Button cadastroUsuarioButton = new Button("Cadastro de Usuário");
        Button editUsuarioButton = new Button("Edição de Usuário");

        Button logoffButton = new Button("Logoff");


        // Defina a visibilidade dos botões com base no tipo de usuário
        if (tipo == App.UserType.ADMINISTRADOR) {
            cadastroUsuarioButton.setVisible(true);
            editUsuarioButton.setVisible(true);
        } else if (tipo == App.UserType.PORTEIRO) {
            cadastroUsuarioButton.setVisible(false);
            editUsuarioButton.setVisible(false);
        }else {
            // Proximos valores
        }

        // Tela de notas fiscais
        nfButton.setOnAction(e -> {
            NFSelect nfSelect = new NFSelect(user.getId());
            nfSelect.start(new Stage());

        });
        // Tela de Visitantes
        visitorButton.setOnAction(e -> {
            VisitorSelect visitorSelect = new VisitorSelect();
            visitorSelect.start(new Stage());

        });
        // Tela de Cadastro de usuário
        cadastroUsuarioButton.setOnAction(e -> {
            CadastroUser cadastroUser = new CadastroUser();
            cadastroUser.start(new Stage());

        });
        // Tela de edição de usuário
        editUsuarioButton.setOnAction(e -> {
            EditUser editUser = new EditUser();
            editUser.start(new Stage());

        });
        // Botão de Logoff
        logoffButton.setOnAction(e -> {
            App app = new App();
            app.start(new Stage());
            primaryStage.close();
        });

        vbox.getChildren().addAll(nameLabel, nfButton, visitorButton, cadastroUsuarioButton, editUsuarioButton, logoffButton);

        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
