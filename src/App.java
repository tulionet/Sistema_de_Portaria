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
    // Tipos de usuário
    public enum UserType {
        ADMINISTRADOR, PORTEIRO
    }

    private UserType userType;
    private User user; 

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

            // Autenticação Banco de dados
             user = authenticate(username, password);
            
            if (user != null) {
                switch (user.getTipo()) {
                    
                    case 1: setUserType(UserType.ADMINISTRADOR); break;
                    case 2: setUserType(UserType.PORTEIRO); break;
                    // case 3: Proximos Valores
                    default:setUserType(UserType.PORTEIRO); break;
                }
                Main main = new Main(user);
                Stage mainStage = new Stage();
                main.start(mainStage);
                primaryStage.close();
            } else {
                showAlert("Falha na autenticação. Verifique suas credenciais.");
            }
            
        });

        vbox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton);

        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private User authenticate(String username, String password) {
        String sql = "SELECT * FROM Usuarios WHERE Usuario = ? AND Senha = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
    
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String nome = resultSet.getString("Nome");
                int tipo = resultSet.getInt("Tipo");
    
                User User = new User(id, nome, username, password, tipo);

                return User;
            } else {
                return null;
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }

    public User getUser(){
        return this.user;
        
    }
}


