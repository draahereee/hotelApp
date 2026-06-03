package hotel.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = getEnvOrDefault("HOTEL_DB_URL", "jdbc:postgresql://localhost:5432/hotelSystem");
    private static final String USER = getEnvOrDefault("HOTEL_DB_USER", "postgres");
    private static final String PASSWORD = getEnvOrDefault("HOTEL_DB_PASSWORD", "postgres");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
