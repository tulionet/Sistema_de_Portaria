package PortaGuard.Visitantes;

import PortaGuard.DB.DatabaseConnection;
import PortaGuard.Usuários.User;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class VisitorSelect extends Application implements VisitorSelectionCallback {

    private TextField cpfFilterField;
    private TextField nomeField;
    private TextField empresaField;
    private TextField cpfField;
    private TextField motivoField;

    private TableView<Visitor> visitorTable;
    private ObservableList<Visitor> visitorList;
    User user;
    public VisitorSelect(User user) {
        this.user = user;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Cadastro e Consulta de Visitantes");


        cpfFilterField = new TextField();
        nomeField = new TextField(); nomeField.setEditable(false);
        empresaField = new TextField(); empresaField.setEditable(false);
        cpfField = new TextField();  cpfField.setEditable(false);
        motivoField = new TextField(); motivoField.setPrefColumnCount(20);
        
        Label cpfFilterLabel = new Label("Filtrar por CPF:");
        Label nomeLabel = new Label("Nome:");
        Label empresaLabel = new Label("Empresa:");
        Label cpfLabel = new Label("CPF:");
        Label motivoLabel = new Label("Motivo:");

        Button filtrarButton = new Button("Filtrar");
        Button cadastrarButton = new Button("Cadastrar Visitante");
        Button editarVisitanteButton = new Button("Editar Visitante");
        Button entradaVisitanteButton = new Button("Entrada Visitante");
        Button saidaVisitanteButton = new Button("Saída Visitante");
        Button voltarButton = new Button("Voltar à tela Principal");
        Button consultarButton = new Button("Consultar Saída");

        voltarButton.setOnAction(e -> stage.close());
        cadastrarButton.setOnAction(e -> {
            cadastrarVisitante();
        });
        entradaVisitanteButton.setOnAction(e -> {
            entradaVisitante("Entrada");
        });

        saidaVisitanteButton.setOnAction(e -> {
            saidaVisitante("Saída");
        });

        filtrarButton.setOnAction(e -> {
            String cpf = cpfFilterField.getText();
            if (cpf.isEmpty()) {
                visitantesCadastrados();
            } else filterVisitorByCPF(cpfFilterField.getText());
        });

        editarVisitanteButton.setOnAction(e -> {
            editarVisitante();
        });

        consultarButton.setOnAction(e ->{
            consultarVisitante();
        });
        

        GridPane displayGrid = new GridPane();
        displayGrid.setHgap(10);
        displayGrid.setVgap(10);
        displayGrid.addRow(1, cpfFilterLabel, cpfFilterField, filtrarButton);
        displayGrid.addRow(2, nomeLabel, nomeField);
        displayGrid.addRow(3, empresaLabel, empresaField);
        displayGrid.addRow(4, cpfLabel, cpfField);
        displayGrid.addRow(5, motivoLabel, motivoField);
        displayGrid.addRow(6, cadastrarButton, editarVisitanteButton);
        displayGrid.addRow(7, entradaVisitanteButton, saidaVisitanteButton);
        displayGrid.addRow(8, voltarButton, consultarButton);
        

        configureTable();

        visitorList = FXCollections.observableArrayList();
        visitorTable.setItems(visitorList);
        
        
        VBox layout = new VBox(20, displayGrid, visitorTable);
        displayGrid.setId("GRID");
        layout.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #336699, #66CCFF);");
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/PortaGuard/Front/CSS/VisitorSelect.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        
        loadtablefromdb();
    }

    private void configureTable() {
        visitorTable = new TableView<>();     

        TableColumn<Visitor, String> dataHoraColumn = new TableColumn<>("Data e Hora");
        dataHoraColumn.setCellValueFactory(new PropertyValueFactory<>("dataHora"));
    
        TableColumn<Visitor, String> situacaoColumn = new TableColumn<>("Situação");
        situacaoColumn.setCellValueFactory(new PropertyValueFactory<>("situacao"));
    
        TableColumn<Visitor, String> nomeColumn = new TableColumn<>("Nome");
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
    
        TableColumn<Visitor, String> empresaColumn = new TableColumn<>("Empresa");
        empresaColumn.setCellValueFactory(new PropertyValueFactory<>("empresa"));
    
        TableColumn<Visitor, String> cpfColumn = new TableColumn<>("CPF");
        cpfColumn.setCellValueFactory(new PropertyValueFactory<>("cpf"));

        TableColumn<Visitor, String> motivoColumn = new TableColumn<>("Motivo");
        motivoColumn.setCellValueFactory(new PropertyValueFactory<>("motivo"));
    
        visitorTable.getColumns().addAll(dataHoraColumn, situacaoColumn, nomeColumn, empresaColumn, cpfColumn, motivoColumn);

    }
    
    
    private void filterVisitorByCPF(String cpf) {
        String sql = "SELECT * FROM Visitantes_Cadastrados WHERE CPF = ?";

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
            } else {
                showAlertErr("Visitante não encontrado para o CPF especificado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertErr("Erro ao carregar dados do banco de dados: " + e.getMessage());
        }
    }

    private void showAlertErr(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertWRG(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
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

    private void cadastrarVisitante() {
        VisitorRegister visitorRegister = new VisitorRegister();
        Stage stage = new Stage();
        visitorRegister.start(stage);
    }

    private void visitantesCadastrados() {
        VisitorRegistered visitorRegistered = new VisitorRegistered();
        visitorRegistered.setSelectionCallback(this);
        Stage stage = new Stage();
        visitorRegistered.start(stage);
    }
    

    public static class Visitor {
        private final SimpleStringProperty dataHora;
        private final SimpleStringProperty situacao;
        private final SimpleStringProperty nome;
        private final SimpleStringProperty empresa;
        private final SimpleStringProperty cpf;
        private final SimpleStringProperty motivo;

        public Visitor(String dataHora, String situacao, String nome, String empresa, String cpf, String motivo) {
            this.dataHora = new SimpleStringProperty(dataHora);
            this.situacao = new SimpleStringProperty(situacao);
            this.nome = new SimpleStringProperty(nome);
            this.empresa = new SimpleStringProperty(empresa);
            this.cpf = new SimpleStringProperty(cpf);
            this.motivo = new SimpleStringProperty(motivo);

        }

        public String getMotivo() {
            return motivo.get();
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

        public String getDataHora() {
            return dataHora.get();
        }

        public String getSituacao() {
            return situacao.get();
        }
    }

    private void entradaVisitante(String situacao) {
        String nome = nomeField.getText();
        String empresa = empresaField.getText();
        String cpf = cpfField.getText();
        String motivo = motivoField.getText();

        if (nome.isEmpty() || empresa.isEmpty() || cpf.isEmpty()) {
            showAlertWRG("Visitante não encontrado. Verifique o CPF.");
        } else if (isVisitanteCadastrado(cpf)) {
            showAlertWRG("Visitante já cadastrado.");
        } else {
            insertVisitantesEnter(nome, empresa, cpf, situacao, motivo);
            loadtablefromdb();
            nomeField.clear();
            empresaField.clear();
            cpfField.clear();
            motivoField.clear();
        }
    }

    private void saidaVisitante(String situacao) {
        Visitor selected = visitorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
        String nome = selected.getNome();
        String empresa = selected.getEmpresa();
        String cpf = selected.getCpf();
                
        insertVisitantesLeave(nome, empresa, cpf, situacao);
        loadtablefromdb();
        } else showAlertWRG("Nada selecionado");


    }

    private void insertVisitantesEnter(String nome, String empresa, String cpf, String situacao, String motivo) {
        String sql = "INSERT INTO Visitantes (Porteiro, Nome, CPF, Empresa, flgIN, Motivo, DataHoraEntrada) VALUES (?, ?, ?, ?, 1, ?, GETDATE())";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setString(2, nome);
            preparedStatement.setString(3, cpf);
            preparedStatement.setString(4, empresa);
            preparedStatement.setString(5, motivo);

    
            preparedStatement.executeUpdate();
            showAlertOK("Movimentação registrada com sucesso como " + situacao);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertErr("Erro ao conectar com o Banco " + e.getMessage());
        }
    }

    private void insertVisitantesLeave(String nome, String empresa, String cpf, String situacao) {
        String sql = "UPDATE Visitantes SET DataHoraSaida = GETDATE(), flgIN = 0, horaTotal = DATEDIFF(MINUTE, DataHoraEntrada, GETDATE()) WHERE Nome = ? AND Empresa = ? AND CPF = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, empresa);
            preparedStatement.setString(3, cpf);
    
            preparedStatement.executeUpdate();
            showAlertOK("Movimentação registrada com sucesso como " + situacao);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertErr("Erro ao conectar com o Banco " + e.getMessage());
        }
    }
    

    private void loadtablefromdb() {
        String sql = "SELECT * FROM Visitantes WHERE flgIN = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Visitor> visitantePendente = new ArrayList<>();

            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("DataHoraEntrada");
                String dataHoraEntrada = (timestamp != null) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp) : "";
                String situacao = resultSet.getString("flgIN");
                String nome = resultSet.getString("Nome");
                String empresa = resultSet.getString("Empresa");
                String cpf = resultSet.getString("CPF");
                String motivo = resultSet.getString("Motivo");

                if (situacao.equals("1") ) {
                    situacao = "Entrada";
                    
                } else { 
                    situacao = "Saída"; }

                    visitantePendente.add(new Visitor(dataHoraEntrada, situacao, nome, empresa, cpf, motivo));
                }
            visitorList.setAll(visitantePendente);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertErr("Erro ao carregar dados do banco de dados: " + e.getMessage());
        }
    }

  
    public void onVisitorSelected(String nome, String empresa, String cpf) {
        nomeField.setText(nome);
        empresaField.setText(empresa);
        cpfField.setText(cpf);
    }

    private boolean isVisitanteCadastrado(String cpf) {
        String sql = "SELECT * FROM Visitantes WHERE CPF = ? AND flgIN = 1";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
            preparedStatement.setString(1, cpf);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertErr("Erro ao verificar se o visitante já está cadastrado: " + e.getMessage());
            return false;
        }
    }
    
    private void editarVisitante() {
        String nome = nomeField.getText();
        String empresa = empresaField.getText();
        String cpf = cpfField.getText();

        if (nome.isEmpty() || empresa.isEmpty() || cpf.isEmpty()) {
            showAlertWRG("Visitante não encontrado. Verifique o CPF.");
        } else {
            VisitorAlter visitorAlter = new VisitorAlter(nome, empresa, cpf);
            Stage stage = new Stage();
            visitorAlter.start(stage);
            nomeField.clear();
            empresaField.clear();
            cpfField.clear();
        }
    }
    
    private void consultarVisitante() {
            VisitorConsulta visitorConsulta = new VisitorConsulta();
            Stage stage = new Stage();
            visitorConsulta.start(stage);
        }
}


