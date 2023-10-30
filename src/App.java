import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class App extends Application {

    public enum UserType {
        ADMINISTRATOR, PORTEIRO
    }

    private String porteiroNome;
    private int userId;
    private UserType userType; // Adicione o tipo de usuário

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Sistema de Login");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Usuário");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");

        Button loginButton = new Button("Conectar");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Verifique a autenticação no banco de dados
            String nomeUsuarioAutenticado = authenticate(username, password);
            if (nomeUsuarioAutenticado != null) {
                // Determine o tipo de usuário com base nas informações de autenticação
                if (username.equals("admin")) {
                    setUserType(UserType.ADMINISTRATOR);
                } else {
                    setUserType(UserType.PORTEIRO);
                }
                Main main = new Main();
                Stage mainStage = new Stage();
                main.start(mainStage);
                primaryStage.close(); // Fecha a tela de login
            } else {
                showAlert("Falha na autenticação. Verifique suas credenciais.");
            }
        });

        vbox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton);

        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String authenticate(String username, String password) {
        String sql = "SELECT Nome FROM Usuarios WHERE Usuario = ? AND Senha = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Nome");
            } else {
                return null; // Autenticação falhou
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Autenticação falhou devido a um erro
        }
    }

    // Método para obter o ID do usuário com base no nome de usuário
    private int getUserIdByUsername(String username) {
        String sql = "SELECT ID FROM Usuarios WHERE Usuario = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("ID");
            } else {
                return -1; // Usuário não encontrado
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Ocorreu um erro ao buscar o ID do usuário
        }
    }

    // Método para mostrar um alerta de erro
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter para o nome do porteiro
    public String getPorteiroNome() {
        return porteiroNome;
    }

    // Getter para o ID do porteiro
    public int getUserId() {
        return userId;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }
}

