package tn.esprit.pidev.Database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Utility class to update the database schema
 */
public class DatabaseSchemaUpdater {

    /**
     * Updates the database schema by executing the SQL script to alter the produit table
     * to properly store binary image data
     */
    public static void updateSchema() {
        try (Connection connection = Database.getConnection()) {
            if (connection != null) {
                // Load the SQL script from resources
                InputStream inputStream = DatabaseSchemaUpdater.class.getResourceAsStream("/tn/esprit/pidev/database/alter_produit_table.sql");
                if (inputStream == null) {
                    System.err.println("Could not find SQL script to update schema");
                    return;
                }
                
                // Read the SQL script
                String sql = new BufferedReader(new InputStreamReader(inputStream))
                        .lines().collect(Collectors.joining("\n"));
                
                // Execute the SQL script
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                    System.out.println("Database schema updated successfully");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}