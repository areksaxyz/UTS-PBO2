package id.ac.utb.pbo2.db;

import id.ac.utb.pbo2.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class BasisData {
    private BasisData() {
    }

    public static Connection serverConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.serverUrl(), AppConfig.DB_USER, AppConfig.DB_PASSWORD);
    }

    public static Connection connection() throws SQLException {
        return DriverManager.getConnection(AppConfig.databaseUrl(), AppConfig.DB_USER, AppConfig.DB_PASSWORD);
    }
}
