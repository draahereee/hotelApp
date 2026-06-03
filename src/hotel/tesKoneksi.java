package hotel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class tesKoneksi {
    // Port standar PostgreSQL adalah 5432
    private static final String URL = "jdbc:postgresql://localhost:5432/hotelSystem";
    private static final String USER = "postgres"; // Username bawaan postgres
    private static final String PASSWORD = "postgres"; 

    public static void main(String[] args) {
        System.out.println("Mencoba menghubungkan ke PostgreSQL 18...");

        // Menggunakan try-with-resources (Fitur Java modern agar koneksi otomatis menutup jika selesai)
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (connection != null) {
                System.out.println("🎉 BERHASIL! Java 21 Anda sudah terhubung ke PostgreSQL 18.");
            }
        } catch (SQLException e) {
            System.err.println("❌ GAGAL terhubung ke database!");
            e.printStackTrace();
        }
    }
}

