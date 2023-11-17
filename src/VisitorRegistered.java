import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VisitorRegistered extends Application {

    private VisitorSelectionCallback selectionCallback;

    private TableView<Visitor> visitorTable;
    private ObservableList<Visitor> visitorList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Visitantes Cadastrados");

        visitorTable = new TableView<>();
        visitorList = FXCollections.observableArrayList();

        configureTable();

        Button confirmButton = new Button("Confirmar Seleção");
        confirmButton.setOnAction(e -> confirmSelection());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(visitorTable, confirmButton);

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.show();

        loadTableFromDB();
    }

    private void configureTable() {
        TableColumn<Visitor, String> nomeColumn = new TableColumn<>("Nome");
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Visitor, String> empresaColumn = new TableColumn<>("Empresa");
        empresaColumn.setCellValueFactory(new PropertyValueFactory<>("empresa"));

        TableColumn<Visitor, String> cpfColumn = new TableColumn<>("CPF");
        cpfColumn.setCellValueFactory(new PropertyValueFactory<>("cpf"));

        visitorTable.getColumns().addAll(nomeColumn, empresaColumn, cpfColumn);
        visitorTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                confirmSelection();
            }
        });

        visitorTable.setItems(visitorList);
    }

    private void loadTableFromDB() {
        String sql = "SELECT * FROM Visitantes_Cadastrados";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Visitor> visitantesCadastrados = new ArrayList<>();

            while (resultSet.next()) {
                String nome = resultSet.getString("Nome");
                String empresa = resultSet.getString("Empresa");
                String cpf = resultSet.getString("CPF");

                visitantesCadastrados.add(new Visitor(nome, empresa, cpf));
            }
            visitorList.setAll(visitantesCadastrados);
        } catch (SQLException e) {
            e.printStackTrace();
    
        }
    }

    private void confirmSelection() {
        Visitor selectedVisitor = visitorTable.getSelectionModel().getSelectedItem();

        if (selectedVisitor != null && selectionCallback != null) {
            selectionCallback.onVisitorSelected(
                selectedVisitor.getNome(),
                selectedVisitor.getEmpresa(),
                selectedVisitor.getCpf()
            );
    
            ((Stage) visitorTable.getScene().getWindow()).close();
        } else {
            System.out.println("Nada selecionado");
        }
    }

    public static class Visitor {
        private final SimpleStringProperty nome;
        private final SimpleStringProperty empresa;
        private final SimpleStringProperty cpf;

        public Visitor(String nome, String empresa, String cpf) {
            this.nome = new SimpleStringProperty(nome);
            this.empresa = new SimpleStringProperty(empresa);
            this.cpf = new SimpleStringProperty(cpf);
        }

        public String getNome() {
            return nome.get();
        }

        public String getEmpresa() {
            return empresa.get();
        }

        public String getCpf() {
            return cpf.get();
        }
    }

    public void setSelectionCallback(VisitorSelectionCallback callback) {
        this.selectionCallback = callback;
    }
}
