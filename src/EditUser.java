import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class EditUser extends Application {

    private TableView<User> userTable;
    private User selectedUser;
    private TextField nomeField;
    private PasswordField senhaField;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Editar Usuário");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Label titleLabel = new Label("Editar Usuário");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        userTable = new TableView<>();
        userTable.setPrefHeight(200);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<User, String> nomeColumn = new TableColumn<>("Nome");
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<User, String> usuarioColumn = new TableColumn<>("Usuário");
        usuarioColumn.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        TableColumn<User, String> senhaColumn = new TableColumn<>("Senha");
        senhaColumn.setCellValueFactory(new PropertyValueFactory<>("senha"));
        senhaColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<User, Integer> tipoColumn = new TableColumn<>("Tipo");
        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        userTable.getColumns().addAll(nomeColumn, usuarioColumn, senhaColumn, tipoColumn);

        senhaColumn.setCellFactory(column -> {
            TableCell<User, String> cell = new TableCell<User, String>() {
                @Override
                protected void updateItem(String senha, boolean empty) {
                    super.updateItem(senha, empty);
                    if (senha == null || empty) {
                        setText(null);
                    } else {
                        setText("*****");
                    }
                }
            };
            return cell;
        });       
        
        tipoColumn.setCellFactory(column -> new TableCell<User, Integer>() {
            @Override
            protected void updateItem(Integer tipo, boolean empty) {
                super.updateItem(tipo, empty);
                
                if (tipo == null || empty) {
                    setText(null);
                } else {
                    try {
                        App.UserType[] values = App.UserType.values();
                        App.UserType perm = null;
                        for (App.UserType value : values) {
                            if (value.ordinal() + 1 == tipo) {
                                perm = value;
                                break;
                            }
                        }
                        if (perm != null) {
                            int nextIndex = (perm.ordinal()) % values.length;
                            App.UserType nextType = values[nextIndex];
                            setText(nextType.name());
                        } else {
                            setText("Tipo desconhecido");
                        }
                    } catch (IllegalArgumentException e) {
                        setText("Tipo desconhecido");
                    }
                }
            }
        });

        nomeField = new TextField();
        nomeField.setPromptText("Nome");

        senhaField = new PasswordField();
        senhaField.setPromptText("Senha");

        
        Button confirmarButton = new Button("Confirmar");
        confirmarButton.setOnAction(e -> {
            if(!isValidPassword(senhaField.getText())){
                showAlert("A senha deve conter pelo menos 6 caracteres, incluindo maiúsculas, minúsculas e números.");
            } else if (!isValidInput(nomeField.getText(), senhaField.getText())) {
                showAlert("Nome e senha não podem ser vazios.");
            } else
                atualizarUsuario();
        });
           
        Button alterarButton = new Button("Alterar");
        alterarButton.setOnAction(e -> alterarUsuario());

        
        Button voltarButton = new Button("Voltar");
        voltarButton.setOnAction(e -> {
            primaryStage.close();
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(alterarButton, confirmarButton);
        
        vbox.getChildren().addAll(titleLabel, userTable, nomeField, senhaField, buttonBox, voltarButton);

        Scene scene = new Scene(vbox, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        preencherTabelaUsuarios();
    }

    private void preencherTabelaUsuarios() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Usuarios");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ObservableList<User> users = FXCollections.observableArrayList();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String nome = resultSet.getString("Nome");
                String usuario = resultSet.getString("Usuario");
                String senha = resultSet.getString("Senha");
                int tipo = resultSet.getInt("Tipo");

                User user = new User(id, nome, usuario, senha, tipo);
                users.add(user);
            }
            userTable.setItems(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void alterarUsuario() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nomeField.setText(selected.getNome());
            senhaField.setText(selected.getSenha());
            selectedUser = selected;
        }
    }

    private void atualizarUsuario() {
        if (selectedUser != null) {
            String novoNome = nomeField.getText();
            String novaSenha = senhaField.getText();
            String usuario = selectedUser.getUsuario();

            atualizarUsuarioNoBanco(usuario, novoNome, novaSenha);

           showAlert("Usuário editado com sucesso!");

            nomeField.clear();
            senhaField.clear();
            selectedUser = null;

            preencherTabelaUsuarios();
        }
    }

    private void atualizarUsuarioNoBanco(String usuario, String novoNome, String novaSenha) {
        String sql = "UPDATE Usuarios SET Nome = ?, Senha = ? WHERE Usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, novoNome);
            preparedStatement.setString(2, novaSenha);
            preparedStatement.setString(3, usuario);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidPassword(String password) {
        // A senha deve ter pelo menos 6 caracteres, incluindo maiúsculas, minúsculas e números
        return password.length() >= 6 && Pattern.compile("[a-z]").matcher(password).find()
                && Pattern.compile("[a-z]").matcher(password).find()
                && Pattern.compile("[A-Z]").matcher(password).find()
                && Pattern.compile("[0-9]").matcher(password).find();
    }

    private boolean isValidInput(String nome, String senha) {
        return !nome.isEmpty() && !senha.isEmpty();
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edição de Usuário");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
