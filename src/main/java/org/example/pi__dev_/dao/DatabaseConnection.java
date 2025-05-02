package org.example.pi__dev_.dao;







import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pi_dev?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // votre mot de passe

    static Connection connection;

    static {
        try {
            // 1. Charge le pilote JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Successfully connected to database");
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to database:");
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database", e);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error while closing connection:");
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Connection test successful!");
        } catch (SQLException e) {
            System.err.println("Connection test failed:");
            e.printStackTrace();
        }
    }
}