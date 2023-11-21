package PortaGuard.Usuários;

import PortaGuard.NotaFiscal.NFSelect;
import PortaGuard.Visitantes.VisitorSelect;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    App.UserType tipo;
    User user;

    public Main(User user, App.UserType tipo) {
        this.user = user;
        this.tipo = tipo;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tela Principal");

        VBox mainVBox = new VBox();
        mainVBox.setAlignment(Pos.CENTER);
        mainVBox.setPadding(new Insets(20));
        mainVBox.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #336699, #66CCFF);");

        Pane centerPane = new Pane();
        centerPane.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        centerPane.setMaxWidth(300);
        centerPane.setMinHeight(250);

        VBox centerVBox = new VBox(10);
        centerVBox.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Bem-vindo, " + user.getNome());
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
        } else {
            // Proximos valores
        }

        // Tela de notas fiscais
        nfButton.setOnAction(e -> {
            NFSelect nfSelect = new NFSelect(user);
            nfSelect.start(new Stage());

        });
        // Tela de Visitantes
        visitorButton.setOnAction(e -> {
            VisitorSelect visitorSelect = new VisitorSelect(user);
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

        centerVBox.getChildren().addAll(nameLabel, nfButton, visitorButton, cadastroUsuarioButton, editUsuarioButton, logoffButton);
        centerPane.getChildren().add(centerVBox);
        mainVBox.getChildren().add(centerPane);
        
        centerVBox.layoutXProperty().bind(centerPane.widthProperty().subtract(centerVBox.widthProperty()).divide(2));
        centerVBox.layoutYProperty().bind(centerPane.heightProperty().subtract(centerVBox.heightProperty()).divide(2));

        Scene scene = new Scene(mainVBox, Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
