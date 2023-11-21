package PortaGuard.Visitantes;

import PortaGuard.DB.DatabaseConnection;
import PortaGuard.NotaFiscal.NFSelect.NF;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class VisitorConsulta extends Application{

    private TableView<Visitor> visitorTable;
    private ObservableList<Visitor> visitorList;

    private TextField porteiroFilter;
    private TextField dataHoraEntradaFilter;
    private TextField dataHoraSaidaFilter;
    private TextField horaTotalFilter;
    private TextField situacaoFilter;
    private TextField nomeFilter;
    private TextField empresaFilter;
    private TextField cpfFilter;
    private TextField motivoFilter;
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Consulta de Visitas");

        configureTable();

        Button confirmButton = new Button("Confirmar Seleção");
        confirmButton.setOnAction(e -> stage.close());

        Button filterButton = new Button("Filtrar");
        filterButton.setOnAction(e -> showFilterDialog());

        HBox buttonBox = new HBox(10, confirmButton, filterButton);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #336699, #66CCFF);");
        layout.getChildren().addAll(visitorTable, buttonBox);

        Scene scene = new Scene(layout, 800, 400);
        scene.getStylesheets().add(getClass().getResource("/PortaGuard/Front/CSS/VisitorSelect.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void configureTable() {
        visitorTable = new TableView<>();

        TableColumn<Visitor, String> porteiroColumn = new TableColumn<>("Porteiro");
        porteiroColumn.setCellValueFactory(new PropertyValueFactory<>("porteiro"));

        TableColumn<Visitor, String> dataHoraEntradaColumn = new TableColumn<>("Data e Hora Entrada");
        dataHoraEntradaColumn.setCellValueFactory(new PropertyValueFactory<>("dataHoraEntrada"));

        TableColumn<Visitor, String> dataHoraSaidaColumn = new TableColumn<>("Data e Hora Saída");
        dataHoraSaidaColumn.setCellValueFactory(new PropertyValueFactory<>("dataHoraSaida"));

        TableColumn<Visitor, String> horaTotalColumn = new TableColumn<>("Tempo presente (Minutos)");
        horaTotalColumn.setCellValueFactory(new PropertyValueFactory<>("horaTotal"));
    
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

        visitorTable.getColumns().addAll(porteiroColumn, dataHoraEntradaColumn, dataHoraSaidaColumn, horaTotalColumn, situacaoColumn, nomeColumn, empresaColumn, cpfColumn, motivoColumn);
        
        visitorList = FXCollections.observableArrayList();
        visitorTable.setItems(visitorList);
    }

    // private void loadTableFromDB() {
    //     String sql = "SELECT b.Nome as Porteiro, a.DataHoraEntrada, a.DataHoraSaida, a.HoraTotal, a.flgIN, a.Nome, a.Empresa, a.CPF, a.Motivo " +
    //     "FROM visitantes a " +
    //     "INNER JOIN Usuarios b ON b.ID = a.Porteiro";

    //     try (Connection connection = DatabaseConnection.getConnection();
    //          PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

    //         ResultSet resultSet = preparedStatement.executeQuery();

    //         List<Visitor> consultaVisitantes = new ArrayList<>();

    //         while (resultSet.next()) {
    //             String porteiro = resultSet.getString("Porteiro");
    //             Timestamp entradaTimestamp = resultSet.getTimestamp("DataHoraEntrada");
    //             Timestamp saidaTimestamp = resultSet.getTimestamp("DataHoraSaida");
    //             String dataHoraEntrada = (entradaTimestamp != null) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entradaTimestamp) : "";
    //             String dataHoraSaida = (saidaTimestamp != null) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(saidaTimestamp) : "";
    //             String horaTotal = resultSet.getString("horaTotal");
    //             String situacao = resultSet.getString("flgIN");
    //             String nome = resultSet.getString("Nome");
    //             String empresa = resultSet.getString("Empresa");
    //             String cpf = resultSet.getString("CPF");
    //             String motivo = resultSet.getString("Motivo");
            
    //             if (situacao.equals("1")) {
    //                 situacao = "Entrada";
    //             } else {
    //                 situacao = "Saída";
    //             }
                
    //             consultaVisitantes.add(new Visitor(porteiro, dataHoraEntrada, dataHoraSaida, horaTotal, situacao, nome, empresa, cpf, motivo));
    //         }
            
    //         visitorList.setAll(consultaVisitantes);
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    
    //     }
    // }

    public static class Visitor {
        private final SimpleStringProperty porteiro;
        private final SimpleStringProperty dataHoraEntrada;
        private final SimpleStringProperty dataHoraSaida;
        private final SimpleStringProperty horaTotal;
        private final SimpleStringProperty situacao;
        private final SimpleStringProperty nome;
        private final SimpleStringProperty empresa;
        private final SimpleStringProperty cpf;
        private final SimpleStringProperty motivo;

        public Visitor(String porteiro, String dataHoraEntrada, String dataHoraSaida, String horaTotal, String situacao, String nome, String empresa, String cpf, String motivo) {
            this.porteiro = new SimpleStringProperty(porteiro);
            this.dataHoraEntrada = new SimpleStringProperty(dataHoraEntrada);
            this.dataHoraSaida = new SimpleStringProperty(dataHoraSaida);
            this.horaTotal = new SimpleStringProperty(horaTotal);
            this.situacao = new SimpleStringProperty(situacao);
            this.nome = new SimpleStringProperty(nome);
            this.empresa = new SimpleStringProperty(empresa);
            this.cpf = new SimpleStringProperty(cpf);
            this.motivo = new SimpleStringProperty(motivo);

        }

        public String getPorteiro(){
            return porteiro.get();
        }

        public String getDataHoraEntrada(){
            return dataHoraEntrada.get();
        }

        public String getDataHoraSaida(){
            return dataHoraSaida.get();
        }

        public String getHoraTotal(){
            return horaTotal.get();
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

        public String getSituacao() {
            return situacao.get();
        }
    }

    private void showFilterDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Filtrar Dados");

        porteiroFilter = new TextField(); porteiroFilter.setPromptText("Porteiro");
        dataHoraEntradaFilter = new TextField(); dataHoraEntradaFilter.setPromptText("Data Hora Entrada");
        dataHoraSaidaFilter = new TextField(); dataHoraSaidaFilter.setPromptText("Data Hora Saída");
        horaTotalFilter = new TextField(); horaTotalFilter.setPromptText("Tempo Presente");
        situacaoFilter = new TextField(); situacaoFilter.setPromptText("Situação");
        nomeFilter = new TextField(); nomeFilter.setPromptText("Nome");
        empresaFilter = new TextField(); empresaFilter.setPromptText("Empresa");
        cpfFilter = new TextField(); cpfFilter.setPromptText("CPF");
        motivoFilter = new TextField(); motivoFilter.setPromptText("Motivo");

         GridPane filterGrid = new GridPane();
        filterGrid.setHgap(10);
        filterGrid.setVgap(10);
        filterGrid.addRow(1, new Label("Porteiro:"), porteiroFilter);
        filterGrid.addRow(2, new Label("Data Hora Entrada:"), dataHoraEntradaFilter);
        filterGrid.addRow(3, new Label("Data Hora Saída:"), dataHoraSaidaFilter);
        filterGrid.addRow(4, new Label("Tempo Presente:"), horaTotalFilter);
        filterGrid.addRow(5, new Label("Situação:"), situacaoFilter);
        filterGrid.addRow(6, new Label("Nome:"), nomeFilter);
        filterGrid.addRow(7, new Label("Empresa:"), empresaFilter);
        filterGrid.addRow(8, new Label("CPF:"), cpfFilter);
        filterGrid.addRow(9, new Label("Motivo:"), motivoFilter);

        dialog.getDialogPane().setContent(filterGrid);

        ButtonType filtrarButtonType = new ButtonType("Filtrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(filtrarButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == filtrarButtonType) {
                loadTableFromDBFilter();
            }
            return null;
        });

        dialog.showAndWait();
    }

    
    private void loadTableFromDBFilter() {
        String sql =
            "SELECT b.Nome as Porteiro, a.DataHoraEntrada, a.DataHoraSaida, a.HoraTotal, a.flgIN, a.Nome, a.Empresa, a.CPF, a.Motivo " +
            "FROM visitantes a " +
            "INNER JOIN Usuarios b ON b.ID = a.Porteiro " +
            "WHERE " +
            "b.Nome LIKE ? AND " +
            "a.DataHoraEntrada LIKE ? AND " +
            "a.DataHoraSaida LIKE ? AND " +
            "a.HoraTotal LIKE ? AND " +
            "a.flgIN LIKE ? AND " +
            "a.Nome LIKE ? AND " +
            "a.Empresa LIKE ? AND " +
            "a.CPF LIKE ? AND " +
            "a.Motivo LIKE ? ";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
            preparedStatement.setString(1, "%" + porteiroFilter.getText() + "%");
            preparedStatement.setString(2, "%" + dataHoraEntradaFilter.getText() + "%");
            preparedStatement.setString(3, "%" + dataHoraSaidaFilter.getText() + "%");
            preparedStatement.setString(4, "%" + horaTotalFilter.getText() + "%");
            preparedStatement.setString(5, "%" + situacaoFilter.getText() + "%");
            preparedStatement.setString(6, "%" + nomeFilter.getText() + "%");
            preparedStatement.setString(7, "%" + empresaFilter.getText() + "%");
            preparedStatement.setString(8, "%" + cpfFilter.getText() + "%");
            preparedStatement.setString(9, "%" + motivoFilter.getText() + "%");
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            List<Visitor> consultaVisitantes = new ArrayList<>();
    
            while (resultSet.next()) {
                String porteiro = resultSet.getString("Porteiro");
                Timestamp entradaTimestamp = resultSet.getTimestamp("DataHoraEntrada");
                Timestamp saidaTimestamp = resultSet.getTimestamp("DataHoraSaida");
                String dataHoraEntrada = (entradaTimestamp != null) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entradaTimestamp) : "";
                String dataHoraSaida = (saidaTimestamp != null) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(saidaTimestamp) : "";
                String horaTotal = resultSet.getString("horaTotal");
                String situacao = resultSet.getString("flgIN");
                String nome = resultSet.getString("Nome");
                String empresa = resultSet.getString("Empresa");
                String cpf = resultSet.getString("CPF");
                String motivo = resultSet.getString("Motivo");

                if (situacao.equals("1")) {
                    situacao = "Entrada";
                } else {
                    situacao = "Saída";
                }
    
                consultaVisitantes.add(new Visitor(porteiro, dataHoraEntrada, dataHoraSaida, horaTotal, situacao, nome, empresa, cpf, motivo));
            }
    
            visitorList.setAll(consultaVisitantes);
    
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao carregar dados do banco de dados.");
            alert.showAndWait();
        }
    }
    
}





