import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VisitorSelect extends Application {

    private String porteiroNome; // Campo para armazenar o nome do porteiro

    private TextField cpfFilterField;
    private TextField nomeField;
    private TextField empresaField;
    private TextField cpfField;

    private TableView<Visitor> visitorTable;
    private ObservableList<Visitor> visitorList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Cadastro e Consulta de Visitantes");

        // Campo de filtro por CPF
        cpfFilterField = new TextField();
        Label cpfFilterLabel = new Label("Filtrar por CPF:");

        // Campos para exibição dos dados do visitante
        nomeField = new TextField();
        empresaField = new TextField();
        cpfField = new TextField();
        nomeField.setEditable(false);
        empresaField.setEditable(false);
        cpfField.setEditable(false);

        Label nomeLabel = new Label("Nome:");
        Label empresaLabel = new Label("Empresa:");
        Label cpfLabel = new Label("CPF:");

        // Botão para cadastrar um novo visitante
        Button cadastrarButton = new Button("Cadastrar Visitante");
        Button entradaVisitanteButton = new Button("Entrada Visitante");
        Button saidaVisitanteButton = new Button("Saída Visitante");

        cadastrarButton.setOnAction(e -> {
            cadastrarVisitante();
        });
        entradaVisitanteButton.setOnAction(e -> {
            registerVisitor("Entrada");
        });

        saidaVisitanteButton.setOnAction(e -> {
            registerVisitor("Saída");
        });

        // Botão para filtrar visitante
        Button filtrarButton = new Button("Filtrar");
        filtrarButton.setOnAction(e -> filterVisitorByCPF(cpfFilterField.getText()));

        // Layout dos campos de filtro
        HBox filterBox = new HBox(10, cpfFilterLabel, cpfFilterField, filtrarButton);

        // Layout dos campos de exibição
        GridPane displayGrid = new GridPane();
        displayGrid.setHgap(10);
        displayGrid.setVgap(10);
        displayGrid.addRow(0, nomeLabel, nomeField);
        displayGrid.addRow(1, empresaLabel, empresaField);
        displayGrid.addRow(2, cpfLabel, cpfField);

        // Configurar a tabela de visitantes
        configureTable();

        // Inicialmente, a lista da tabela estará vazia
        visitorList = FXCollections.observableArrayList();
        visitorTable.setItems(visitorList);

        // Layout principal
        VBox layout = new VBox(20, filterBox, displayGrid, cadastrarButton, entradaVisitanteButton, saidaVisitanteButton, visitorTable);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public void setPorteiroUser(String nome) {
        this.porteiroNome = nome;
    }

    private void configureTable() {
        visitorTable = new TableView<>();

        TableColumn<Visitor, String> nomeColumn = new TableColumn<>("Nome");
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Visitor, String> empresaColumn = new TableColumn<>("Empresa");
        empresaColumn.setCellValueFactory(new PropertyValueFactory<>("empresa"));

        TableColumn<Visitor, String> cpfColumn = new TableColumn<>("CPF");
        cpfColumn.setCellValueFactory(new PropertyValueFactory<>("cpf"));

        visitorTable.getColumns().addAll(nomeColumn, empresaColumn, cpfColumn);
    }
    

    private Visitor filterVisitorByCPF(String cpf) {
        String sql = "SELECT Nome, Empresa, CPF FROM Visitantes_Cadastrados WHERE CPF = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, cpf);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String nome = resultSet.getString("Nome");
                String empresa = resultSet.getString("Empresa");
                String cpfResult = resultSet.getString("CPF");
                nomeField.setText(nome);
                empresaField.setText(empresa);
                cpfField.setText(cpfResult);
                return new Visitor(nome, empresa, cpfResult);
            } else {
                // Visitante não encontrado, exiba uma mensagem de erro
                showAlert("Visitante não encontrado para o CPF especificado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Falha ao consultar o banco de dados.");
        }

        return null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método para cadastrar um novo visitante
    private void cadastrarVisitante() {
        VisitorRegister visitorRegister = new VisitorRegister();
        Stage stage = new Stage();
        visitorRegister.start(stage);
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

    private void registerVisitor(String situacao) {
        String cpf = cpfFilterField.getText();

        // Verifique se o CPF existe na tabela de Visitantes_Cadastrados
        Visitor visitor = filterVisitorByCPF(cpf);
        if (visitor == null) {
            showAlert("Visitante não encontrado. Verifique o CPF.");
            return;
        }

        // Registre a entrada ou saída do visitante na tabela de Movimentacoes
        if (insertVisitantes(porteiroNome, visitor.getNome(), visitor.getEmpresa(), visitor.getCpf(), situacao)) {
            showAlert("Movimentação registrada com sucesso como " + situacao);
        } else {
            showAlert("Falha ao registrar a movimentação.");
        }
    }

    private boolean insertVisitantes(String porteiroNome, String nome, String empresa, String cpf, String situacao) {
        String sql = "INSERT INTO Visitantes (Porteiro, Nome, CPF, Situacao, DataHora, Empresa) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
            preparedStatement.setString(1, porteiroNome);
            preparedStatement.setString(2, nome);
            preparedStatement.setString(3, cpf);
            preparedStatement.setString(4, situacao);
    
            // Obtenha a data e hora atual para registrar na movimentação
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
    
            preparedStatement.setString(5, formattedDateTime);
            preparedStatement.setString(6, empresa);
    
            int rowsAffected = preparedStatement.executeUpdate();
    
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Falha ao inserir a movimentação
        }
    }
}
