package hotel.database;

import java.sql.Connection;
import java.sql.SQLException;

public class tesKoneksi {
    public static void main(String[] args) {
        System.out.println("Mencoba menghubungkan ke PostgreSQL 18...");

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                System.out.println("🎉 BERHASIL! Java 21 Anda sudah terhubung ke PostgreSQL 18.");
            }
        } catch (SQLException e) {
            System.err.println("❌ GAGAL terhubung ke database!");
            e.printStackTrace();
        }
    }
}

