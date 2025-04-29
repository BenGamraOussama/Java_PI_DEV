package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private final String URL="jdbc:mysql://localhost:3306/projet_symfony";
    private final String USER="root";
    private final String PSW="";

    private Connection connection;
    private static MyDatabase instance;

    private MyDatabase(){
        try {
            connection = DriverManager.getConnection(URL,USER,PSW);
            System.out.println("✅ Connected to DB: " + connection);
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
        }
    }


    public static MyDatabase getInstance(){
        if(instance == null)
            instance = new MyDatabase();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
