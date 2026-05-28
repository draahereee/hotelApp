package hotel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/masukkan_nama_database_anda"; //ganti dengan URL database Anda
    private static final String USER = "postgres";
    private static final String PASSWORD = "your_password_here"; //masukkan password database Anda

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}