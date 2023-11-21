package PortaGuard.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=Portaria;trustServerCertificate=true";
    private static final String USER = "Portaria";
    private static final String PASSWORD = "Portaria@2023";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
