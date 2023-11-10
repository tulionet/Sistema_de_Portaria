import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class CadastroUser extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Cadastro de Usuário");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Cadastro de Usuário");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome");

        TextField usuarioField = new TextField();
        usuarioField.setPromptText("Usuário");

        PasswordField senhaField = new PasswordField();
        senhaField.setPromptText("Senha");

        PasswordField confirmSenhaField = new PasswordField();
        confirmSenhaField.setPromptText("Confirmar Senha");

        Button voltarButton = new Button("Voltar");
        Button cadastrarButton = new Button("Cadastrar");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.getChildren().addAll(voltarButton, cadastrarButton);

        cadastrarButton.setOnAction(e -> {
            String nome = nomeField.getText();
            String usuario = usuarioField.getText();
            String senha = senhaField.getText();
            String confirmSenha = confirmSenhaField.getText();

            if (nome.isEmpty() || usuario.isEmpty() || senha.isEmpty() || confirmSenha.isEmpty()) {
                showAlert("Todos os campos devem ser preenchidos.");
            } else if (!isValidPassword(senha)) {
                showAlert("A senha deve conter pelo menos 6 caracteres, incluindo maiúsculas, minúsculas e números.");
            } else if (!senha.equals(confirmSenha)) {
                showAlert("As senhas não coincidem.");
            } else {
                if (cadastrarUsuario(nome, usuario, senha)) {
                    showAlert("Usuário cadastrado com sucesso!");
                    // Limpa os campos de entrada para que o usuário possa cadastrar outro usuário
                    nomeField.clear();
                    usuarioField.clear();
                    senhaField.clear();
                    confirmSenhaField.clear();
                } else {
                    showAlert("Falha ao cadastrar o usuário.");
                }
            }
        });

        voltarButton.setOnAction(e -> {
            stage.close();
        });

        vbox.getChildren().addAll(titleLabel, nomeField, usuarioField, senhaField, confirmSenhaField, buttonBox);

        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    private static boolean isValidPassword(String password) {
        // A senha deve ter pelo menos 6 caracteres, incluindo maiúsculas, minúsculas e números
        return password.length() >= 6 && Pattern.compile("[a-z]").matcher(password).find()
                && Pattern.compile("[A-Z]").matcher(password).find()
                && Pattern.compile("[0-9]").matcher(password).find();
    }

    private static boolean cadastrarUsuario(String nome, String usuario, String senha) {
        String sql = "INSERT INTO Usuarios (Nome, Usuario, Senha) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, usuario);
            preparedStatement.setString(3, senha);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro ao cadastrar o usuário: " + e.getMessage());
            return false;
        }
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Cadastro de Usuário");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
