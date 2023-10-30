import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        userTable.getColumns().addAll(nomeColumn, usuarioColumn, senhaColumn);

        nomeField = new TextField();
        nomeField.setPromptText("Nome");

        senhaField = new PasswordField();
        senhaField.setPromptText("Senha");

        Button confirmarButton = new Button("Confirmar");
        confirmarButton.setOnAction(e -> atualizarUsuario());

        Button filtrarButton = new Button("Filtrar");
        filtrarButton.setOnAction(e -> filtrarUsuario());

        vbox.getChildren().addAll(titleLabel, userTable, nomeField, senhaField, filtrarButton, confirmarButton);

        Scene scene = new Scene(vbox, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Preenche a tabela com os dados dos usuários do banco de dados
        preencherTabelaUsuarios();
    }

    private void preencherTabelaUsuarios() {
        // Conecta ao banco de dados e recupera os dados da tabela Usuarios
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Usuarios");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ObservableList<User> users = FXCollections.observableArrayList();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String nome = resultSet.getString("Nome");
                String usuario = resultSet.getString("Usuario");
                String senha = resultSet.getString("Senha");

                User user = new User(id, nome, usuario, senha);
                users.add(user);
            }

            userTable.setItems(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filtrarUsuario() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Preenche automaticamente o nome e a senha com os dados do usuário selecionado
            nomeField.setText(selected.getNome());
            senhaField.setText(selected.getSenha());
            selectedUser = selected;
        }
    }

    private void atualizarUsuario() {
        if (selectedUser != null) {
            // Obtém os novos valores dos campos de nome e senha
            String novoNome = nomeField.getText();
            String novaSenha = senhaField.getText();
            String usuario = selectedUser.getUsuario();

            // Atualiza o usuário no banco de dados com as novas informações com base no nome de usuário
            atualizarUsuarioNoBanco(usuario, novoNome, novaSenha);

            // Exibe uma mensagem de confirmação
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Edição de Usuário");
            alert.setHeaderText(null);
            alert.setContentText("Usuário editado com sucesso!");
            alert.showAndWait();

            // Limpa os campos e a seleção
            nomeField.clear();
            senhaField.clear();
            selectedUser = null;

            // Atualiza a tabela com os dados atualizados do banco de dados
            preencherTabelaUsuarios();
        }
    }

    private void atualizarUsuarioNoBanco(String usuario, String novoNome, String novaSenha) {
        // Atualiza o usuário no banco de dados com as novas informações com base no nome de usuário
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
}
