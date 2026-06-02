package hotel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/masukkan nama database anda"; //ganti dengan URL database Anda
    private static final String USER = "isi user database anda"; //ganti dengan username database Anda
    private static final String PASSWORD = "isi password database anda"; //ganti dengan password database Anda
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}