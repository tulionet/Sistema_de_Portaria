import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VisitorAlter extends Application {

    private String nome;
    private String empresa;
    private String cpf;

    public VisitorAlter(String nome, String empresa, String cpf) {
        this.nome = nome;
        this.empresa = empresa;
        this.cpf = cpf;
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Editar Visitante");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label nomeLabel = new Label("Nome:");
        TextField nomeField = new TextField(nome);

        Label empresaLabel = new Label("Empresa:");
        TextField empresaField = new TextField(empresa);

        Label cpfLabel = new Label("CPF:");
        TextField cpfField = new TextField(cpf);
        cpfField.setEditable(false);

        Button salvarButton = new Button("Salvar Edição");
        salvarButton.setOnAction(e -> {
            try {
                if (validateInputs(nomeField.getText(), empresaField.getText())) {
                    alterVisitanteDB(nomeField.getText(), empresaField.getText(), cpf);
                    showAlertOK("Edição salva com sucesso.");
                } else {
                    showAlertErr("Nome e empresa não podem ser vazios.");
                }
            } catch (SQLException ex) {
                showAlertErr("Erro ao salvar edição: " + ex.getMessage());
            }
        });

        Button voltarButton = new Button("Voltar");
        voltarButton.setOnAction(e -> stage.close());

        gridPane.add(nomeLabel, 0, 0);
        gridPane.add(nomeField, 1, 0);
        gridPane.add(empresaLabel, 0, 1);
        gridPane.add(empresaField, 1, 1);
        gridPane.add(cpfLabel, 0, 2);
        gridPane.add(cpfField, 1, 2);
        gridPane.add(salvarButton, 1, 3);
        gridPane.add(voltarButton, 2, 3);

        Scene scene = new Scene(gridPane, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void alterVisitanteDB(String nome, String empresa, String cpf) throws SQLException {
        String sql = "UPDATE Visitantes_Cadastrados SET nome = ?, empresa = ? WHERE cpf = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, empresa);
            preparedStatement.setString(3, cpf);
            preparedStatement.executeUpdate();

        }
    }

    private void showAlertErr(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertOK(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private boolean validateInputs(String nome, String empresa) {
        return !nome.isEmpty() && !empresa.isEmpty();
    }
}
