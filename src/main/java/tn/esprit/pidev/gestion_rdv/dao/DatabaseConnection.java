package tn.esprit.pidev.gestion_rdv.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pi_dev?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // votre mot de passe

    private static Connection connection;

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

                // Execute SQL scripts to ensure database schema is up to date
                executeSqlScript("sql/alter_consultation_table.sql");
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to database:");
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database", e);
        }
        return connection;
    }

    private static void executeSqlScript(String resourcePath) {
        // Try multiple ways to load the resource
        InputStream is = null;

        // Try with class loader
        is = DatabaseConnection.class.getClassLoader().getResourceAsStream(resourcePath);

        // If not found, try with absolute path from resources
        if (is == null) {
            is = DatabaseConnection.class.getClassLoader().getResourceAsStream("/"+resourcePath);
        }

        // If still not found, try with different path format
        if (is == null) {
            String altPath = resourcePath.replace('/', '\\');
            is = DatabaseConnection.class.getClassLoader().getResourceAsStream(altPath);
        }

        // If still not found, try with absolute path and different format
        if (is == null) {
            String altPath = "/" + resourcePath.replace('/', '\\');
            is = DatabaseConnection.class.getClassLoader().getResourceAsStream(altPath);
        }

        // If still not found, try with direct file access
        if (is == null) {
            try {
                // Try to find the file in the resources directory
                String filePath = "src\\main\\resources\\" + resourcePath.replace('/', '\\');
                is = new java.io.FileInputStream(filePath);
                System.out.println("Found SQL script at: " + filePath);
            } catch (IOException e) {
                System.err.println("SQL script not found at file path: " + resourcePath);
            }
        }

        if (is == null) {
            System.err.println("SQL script not found: " + resourcePath);
            return;
        }

        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                 Statement stmt = connection.createStatement()) {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip comments and empty lines
                    if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                        continue;
                    }

                    sb.append(line);

                    // Execute when semicolon is found
                    if (line.trim().endsWith(";")) {
                        String sql = sb.toString();
                        try {
                            stmt.execute(sql);
                            System.out.println("Executed SQL: " + sql);
                        } catch (SQLException e) {
                            System.err.println("Error executing SQL: " + sql);
                            e.printStackTrace();
                        }
                        sb = new StringBuilder();
                    }
                }
            }
        } catch (IOException | SQLException e) {
            System.err.println("Error executing SQL script: " + resourcePath);
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
