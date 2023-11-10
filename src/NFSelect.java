import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NFSelect extends Application {

    private TextField nomeField;
    private TextField cpfField;
    private TextField placaCarroField;
    private TextField empresaField;
    private TextField nfField;

    private TextField dataHoraFilter;
    private TextField situacaoFilter;
    private TextField nfFilter;
    private TextField nomeFilter;
    private TextField cpfFilter;
    private TextField empresaFilter;
    private TextField placaCarroFilter;
    private TextField porteiroRespFilter;

    private TableView<NF> nfTable;
    private ObservableList<NF> nfList;

    private Button entradaButton;
    private Button saidaButton;
    private Button filtrarButton;
    private Button voltarButton;

    private int id;
    public NFSelect(int id) {
        this.id = id;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Sistema de Entrada e Saída de NF");

        nomeField = new TextField();
        cpfField = new TextField();
        placaCarroField = new TextField();
        empresaField = new TextField();
        nfField = new TextField();

        Label nomeLabel = new Label("Nome:");
        Label cpfLabel = new Label("CPF:");
        Label placaCarroLabel = new Label("Placa do Carro:");
        Label empresaLabel = new Label("Empresa:");
        Label nfLabel = new Label("NF:");

        entradaButton = new Button("Entrada NotaFiscal");
        saidaButton = new Button("Saída NotaFiscal");
        filtrarButton = new Button("Filtrar");
        voltarButton = new Button("Voltar à tela Principal");

        entradaButton.setOnAction(e -> {
            registerNF("Entrada");
        });

        saidaButton.setOnAction(e -> {
            registerNF("Saída");
        });

        filtrarButton.setOnAction(e -> {
            showFilterDialog();
        });

        voltarButton.setOnAction(e -> {
            stage.close();
        });

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.addRow(0, nomeLabel, nomeField);
        inputGrid.addRow(1, cpfLabel, cpfField);
        inputGrid.addRow(2, placaCarroLabel, placaCarroField);
        inputGrid.addRow(3, empresaLabel, empresaField);
        inputGrid.addRow(4, nfLabel, nfField);

        HBox buttonBox = new HBox(10, entradaButton, saidaButton, filtrarButton, voltarButton);

        VBox layout = new VBox(20);
        layout.getChildren().addAll(inputGrid, buttonBox);

        configureTable();

        nfList = FXCollections.observableArrayList();
        nfTable.setItems(nfList);

        layout.getChildren().add(nfTable);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void registerNF(String situacao) {
        String nome = nomeField.getText();
        String cpf = cpfField.getText();
        String placaCarro = placaCarroField.getText().trim().toUpperCase();
        String empresa = empresaField.getText();
        String nf = nfField.getText();

        if (nome.isEmpty() || cpf.isEmpty() || placaCarro.isEmpty() || empresa.isEmpty() || nf.isEmpty()) {
            showAlertWRG("Todos os campos devem ser preenchidos.");
            return;
        }

        if (!isValidCPF(cpf) || !isValidPlaca(placaCarro)) {
            showAlertWRG("CPF inválido ou placa de carro inválida.");
            return;
        }

        addNFToDatabase(situacao, nf, nome, cpf, empresa, placaCarro);
        clearFields();
        showAlertOK("NF registrada com sucesso como " + situacao);
    }

    private void addNFToDatabase(String situacao, String nf, String nome, String cpf, String empresa, String placaCarro) {
        String sql = "INSERT INTO NFs (Situacao, NF, Nome, CPF, Empresa, PlacaCarro, porteiroResp, DataHora) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, situacao);
            preparedStatement.setString(2, nf);
            preparedStatement.setString(3, nome);
            preparedStatement.setString(4, cpf);
            preparedStatement.setString(5, empresa);
            preparedStatement.setString(6, placaCarro);
            preparedStatement.setInt(7, id);
            



            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertErr("Erro ao inserir NF no banco de dados: " + e.getMessage());
        }
    }

    private void showFilterDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Filtrar Dados");

        dataHoraFilter = new TextField();
        situacaoFilter = new TextField();
        nfFilter = new TextField();
        nomeFilter = new TextField();
        cpfFilter = new TextField();
        empresaFilter = new TextField();
        placaCarroFilter = new TextField();


        dataHoraFilter.setPromptText("Data e Hora");
        situacaoFilter.setPromptText("Situação");
        nfFilter.setPromptText("NF");
        nomeFilter.setPromptText("Nome");
        cpfFilter.setPromptText("CPF");
        empresaFilter.setPromptText("Empresa");
        placaCarroFilter.setPromptText("Placa do Carro");


        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(10);
        filterGrid.setVgap(10);
        filterGrid.addRow(0, new Label("Data e Hora:"), dataHoraFilter);
        filterGrid.addRow(1, new Label("Situação:"), situacaoFilter);
        filterGrid.addRow(2, new Label("NF:"), nfFilter);
        filterGrid.addRow(3, new Label("Nome:"), nomeFilter);
        filterGrid.addRow(4, new Label("CPF:"), cpfFilter);
        filterGrid.addRow(5, new Label("Empresa:"), empresaFilter);
        filterGrid.addRow(6, new Label("Placa do Carro:"), placaCarroFilter);


        dialog.getDialogPane().setContent(filterGrid);

        ButtonType filtrarButtonType = new ButtonType("Filtrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(filtrarButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == filtrarButtonType) {
                loadNFsFromDatabaseWithFilter();
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void configureTable() {
        nfTable = new TableView<>();

        TableColumn<NF, String> dataHoraColumn = new TableColumn<>("Data e Hora");
        dataHoraColumn.setCellValueFactory(new PropertyValueFactory<>("dataHora"));

        TableColumn<NF, String> situacaoColumn = new TableColumn<>("Situação");
        situacaoColumn.setCellValueFactory(new PropertyValueFactory<>("situacao"));

        TableColumn<NF, String> nfColumn = new TableColumn<>("NF");
        nfColumn.setCellValueFactory(new PropertyValueFactory<>("nf"));

        TableColumn<NF, String> nomeColumn = new TableColumn<>("Nome");
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<NF, String> cpfColumn = new TableColumn<>("CPF");
        cpfColumn.setCellValueFactory(new PropertyValueFactory<>("cpf"));

        TableColumn<NF, String> empresaColumn = new TableColumn<>("Empresa");
        empresaColumn.setCellValueFactory(new PropertyValueFactory<>("empresa"));

        TableColumn<NF, String> placaCarroColumn = new TableColumn<>("Placa do Carro");
        placaCarroColumn.setCellValueFactory(new PropertyValueFactory<>("placaCarro"));

        TableColumn<NF, String> porteiroResp = new TableColumn<>("Responsável");
        porteiroResp.setCellValueFactory(new PropertyValueFactory<>("porteiroResp"));

        nfTable.getColumns().addAll(dataHoraColumn, situacaoColumn, nfColumn, nomeColumn, cpfColumn, empresaColumn, placaCarroColumn, porteiroResp);
    }

    public static class NF {
        private final SimpleStringProperty dataHora;
        private final SimpleStringProperty situacao;
        private final SimpleStringProperty nf;
        private final SimpleStringProperty nome;
        private final SimpleStringProperty cpf;
        private final SimpleStringProperty empresa;
        private final SimpleStringProperty placaCarro;
        private final SimpleStringProperty porteiroResp;

        public NF(String dataHora, String situacao, String nf, String nome, String cpf, String empresa, String placaCarro, String porteiroResp) {
            this.dataHora = new SimpleStringProperty(dataHora);
            this.situacao = new SimpleStringProperty(situacao);
            this.nf = new SimpleStringProperty(nf);
            this.nome = new SimpleStringProperty(nome);
            this.cpf = new SimpleStringProperty(cpf);
            this.empresa = new SimpleStringProperty(empresa);
            this.placaCarro = new SimpleStringProperty(placaCarro);
            this.porteiroResp = new SimpleStringProperty(porteiroResp);
        }

        public String getDataHora() {
            return dataHora.get();
        }

        public String getSituacao() {
            return situacao.get();
        }

        public String getNf() {
            return nf.get();
        }

        public String getNome() {
            return nome.get();
        }

        public String getCpf() {
            return cpf.get();
        }

        public String getEmpresa() {
            return empresa.get();
        }

        public String getPlacaCarro() {
            return placaCarro.get();
        }

        public String getporteiroResp() {
            return porteiroResp.get();
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

    private void clearFields() {
        nomeField.clear();
        cpfField.clear();
        placaCarroField.clear();
        empresaField.clear();
        nfField.clear();
    }

    private boolean isValidCPF(String cpf) {
        return cpf.matches("\\d{11}");
    }

    private boolean isValidPlaca(String placa) {
        return placa.length() == 7;
    }

    private void loadNFsFromDatabaseWithFilter() {
        String sql =  
                "SELECT * FROM NFs WHERE " +
                "DataHora LIKE ? AND " +
                "Situacao LIKE ? AND " +
                "NF LIKE ? AND " +
                "Nome LIKE ? AND " +
                "CPF LIKE ? AND " +
                "Empresa LIKE ? AND " +
                "PlacaCarro LIKE ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, "%" + dataHoraFilter.getText() + "%");
            preparedStatement.setString(2, "%" + situacaoFilter.getText() + "%");
            preparedStatement.setString(3, "%" + nfFilter.getText() + "%");
            preparedStatement.setString(4, "%" + nomeFilter.getText() + "%");
            preparedStatement.setString(5, "%" + cpfFilter.getText() + "%");
            preparedStatement.setString(6, "%" + empresaFilter.getText() + "%");
            preparedStatement.setString(7, "%" + placaCarroFilter.getText() + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            List<NF> filteredNFList = new ArrayList<>();

            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("DataHora");
                String dataHora = (timestamp != null) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp) : "";
                String situacao = resultSet.getString("Situacao");
                String nf = resultSet.getString("NF");
                String nome = resultSet.getString("Nome");
                String cpf = resultSet.getString("CPF");
                String empresa = resultSet.getString("Empresa");
                String placaCarro = resultSet.getString("PlacaCarro");
                String porteiroResp = resultSet.getString("porteiroResp");

                filteredNFList.add(new NF(dataHora, situacao, nf, nome, cpf, empresa, placaCarro, porteiroResp));
            }

            nfList.setAll(filteredNFList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlertErr("Erro ao carregar dados do banco de dados: " + e.getMessage());
        }
    }
}
