package PortaGuard.Visitantes;

import PortaGuard.DB.DatabaseConnection;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class VisitorRegister extends Application {

    private TextField nomeField;
    private TextField cpfField;
    private TextField empresaField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Cadastro de Visitante");

        Label nomeLabel = new Label("Nome:");
        nomeField = new TextField();

        Label cpfLabel = new Label("CPF (11 dígitos numéricos):");
        cpfField = new TextField();

        Label empresaLabel = new Label("Empresa (opcional):");
        empresaField = new TextField();

        Button cadastrarButton = new Button("Cadastrar");
        cadastrarButton.setOnAction(e -> cadastrarVisitante());

        Button voltarButton = new Button("Voltar");
        voltarButton.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(10, cadastrarButton, voltarButton);

        VBox layout = new VBox(20, nomeLabel, nomeField, cpfLabel, cpfField, empresaLabel, empresaField, buttonBox);
        layout.setId("LayoutREG");
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #336699, #66CCFF);");

        Scene scene = new Scene(layout, 400, 350);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/PortaGuard/Front/CSS/VisitorSelect.css").toExternalForm());
        stage.show();
    }

    private void cadastrarVisitante() {
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String empresa = empresaField.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty()) {
            showAlert("Nome e CPF são campos obrigatórios.");
            return;
        }

        if (!isValidCPF(cpf)) {
            showAlert("CPF inválido. Certifique-se de que tenha 11 dígitos.");
            return;
        }

        if (empresa.isEmpty()) {
            empresa = "N/A";
        }

        if (insertVisitanteCadastrado(nome, cpf, empresa)) {
            showAlert("Visitante cadastrado com sucesso.");
            clearFields();
        } else {
            showAlert("Falha ao cadastrar o visitante.");
        }
    }

    private boolean insertVisitanteCadastrado(String nome, String cpf, String empresa) {
        String sql = "INSERT INTO Visitantes_Cadastrados (Nome, CPF, Empresa) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, cpf);
            preparedStatement.setString(3, empresa);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cadastro de Visitante");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nomeField.clear();
        cpfField.clear();
        empresaField.clear();
    }

    private boolean isValidCPF(String cpf) {
        return Pattern.matches("\\d{11}", cpf);
    }
}
