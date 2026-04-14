package id.ac.utb.pbo2;

import id.ac.utb.pbo2.db.BasisData;
import id.ac.utb.pbo2.db.DatabaseBootstrap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseCheck {
    public static void main(String[] args) throws Exception {
        DatabaseBootstrap.ensureDatabase();
        try (Connection connection = BasisData.connection();
             Statement statement = connection.createStatement()) {
            printCount(statement, "mahasiswa");
            printCount(statement, "dosen");
            printCount(statement, "prodi");
            printCount(statement, "matakuliah");
            printCount(statement, "pembayaran_ukt");
            printCount(statement, "krs");
            printCount(statement, "nilai");
            try (ResultSet resultSet = statement.executeQuery("""
                    SELECT semester, COUNT(*) AS total
                    FROM matakuliah
                    GROUP BY semester
                    ORDER BY semester
                    """)) {
                while (resultSet.next()) {
                    System.out.println("matakuliah_semester_" + resultSet.getInt("semester")
                            + "=" + resultSet.getInt("total"));
                }
            }
        }
    }

    private static void printCount(Statement statement, String table) throws Exception {
        try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            resultSet.next();
            System.out.println(table + "=" + resultSet.getInt(1));
        }
    }
}
