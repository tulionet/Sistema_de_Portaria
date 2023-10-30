import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class Main extends Application {

    public enum UserType {
        ADMINISTRATOR, PORTEIRO
    }
    

    private boolean hasSpecialPermissions; // Flag para verificar permissões especiais

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tela Principal");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        App app = new App();
        String porteiroNome = app.getPorteiroNome();
        int userId = app.getUserId();

        // Exiba o nome do porteiro obtido do campo
        Label nameLabel = new Label("Bem-vindo, " + porteiroNome);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Adicione botões para Entrada de Nota Fiscal e Entrada de Visitante
        Button nfButton = new Button("Entrada Nota Fiscal");
        Button visitorButton = new Button("Entrada Visitante");

        // Crie botões de Cadastro de Usuário e Edição de Usuário
        Button cadastroUsuarioButton = new Button("Cadastro de Usuário");
        Button editUsuarioButton = new Button("Edição de Usuário");

        // Defina a visibilidade dos botões com base nas permissões do usuário
        if (hasSpecialPermissions) {
            cadastroUsuarioButton.setVisible(true);
            editUsuarioButton.setVisible(true);
        } else {
            cadastroUsuarioButton.setVisible(false);
            editUsuarioButton.setVisible(false);
        }
        // Tela de notas fiscais
        nfButton.setOnAction(e -> {
            NFSelect nfSelect = new NFSelect();
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

        vbox.getChildren().addAll(nameLabel, nfButton, visitorButton, cadastroUsuarioButton, editUsuarioButton);

        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Defina as permissões especiais com base no userId
        if (userId == 1) {
            setHasSpecialPermissions(true);
        } else {
            setHasSpecialPermissions(false);
        }
    }

    public void setHasSpecialPermissions(boolean hasSpecialPermissions) {
        this.hasSpecialPermissions = hasSpecialPermissions;
    }
}
