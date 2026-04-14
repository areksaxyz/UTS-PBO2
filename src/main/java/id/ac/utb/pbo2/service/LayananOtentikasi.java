package id.ac.utb.pbo2.service;

import id.ac.utb.pbo2.db.BasisData;
import id.ac.utb.pbo2.model.PenggunaSaatIni;
import id.ac.utb.pbo2.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class LayananOtentikasi {
    public PenggunaSaatIni login(String username, char[] password) throws SQLException {
        String sql = """
                SELECT id, username, nama_lengkap, role, password_hash
                FROM users
                WHERE username = ?
                """;
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                int userId = resultSet.getInt("id");
                String storedHash = resultSet.getString("password_hash");
                if (!PasswordUtil.matches(password, storedHash)) {
                    return null;
                }
                rehashIfLegacy(connection, userId, storedHash, password);
                return new PenggunaSaatIni(
                        userId,
                        resultSet.getString("username"),
                        resultSet.getString("nama_lengkap"),
                        resultSet.getString("role")
                );
            }
        }
    }

    public void changePassword(int userId, char[] oldPassword, char[] newPassword) throws SQLException {
        validateChangePasswordInput(userId, oldPassword, newPassword);
        String selectSql = "SELECT password_hash FROM users WHERE id = ?";
        try (Connection connection = BasisData.connection();
             PreparedStatement select = connection.prepareStatement(selectSql)) {
            select.setInt(1, userId);
            try (ResultSet resultSet = select.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("User tidak ditemukan.");
                }
                String storedHash = resultSet.getString("password_hash");
                if (!PasswordUtil.matches(oldPassword, storedHash)) {
                    throw new SQLException("Password lama tidak sesuai.");
                }
            }

            String updateSql = "UPDATE users SET password_hash = ? WHERE id = ?";
            try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                update.setString(1, PasswordUtil.hash(newPassword));
                update.setInt(2, userId);
                int affected = update.executeUpdate();
                if (affected == 0) {
                    throw new SQLException("User tidak ditemukan.");
                }
            }
        }
    }

    public void createUser(String username, char[] password, String role, String namaLengkap) throws SQLException {
        String sql = """
                INSERT INTO users (username, password_hash, role, nama_lengkap)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            statement.setString(2, PasswordUtil.hash(password));
            statement.setString(3, role);
            statement.setString(4, namaLengkap.trim());
            statement.executeUpdate();
        }
    }

    private void rehashIfLegacy(Connection connection, int userId, String storedHash, char[] password) throws SQLException {
        if (!PasswordUtil.needsRehash(storedHash)) {
            return;
        }
        String updateSql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setString(1, PasswordUtil.hash(password));
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    private void validateChangePasswordInput(int userId, char[] oldPassword, char[] newPassword) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User tidak valid.");
        }
        if (oldPassword == null || oldPassword.length == 0) {
            throw new IllegalArgumentException("Password lama wajib diisi.");
        }
        if (newPassword == null || newPassword.length == 0) {
            throw new IllegalArgumentException("Password baru wajib diisi.");
        }
        if (newPassword.length < 5) {
            throw new IllegalArgumentException("Password baru minimal 5 karakter.");
        }
        if (Arrays.equals(oldPassword, newPassword)) {
            throw new IllegalArgumentException("Password baru harus berbeda dari password lama.");
        }
    }
}
