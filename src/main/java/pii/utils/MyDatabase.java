package pii.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private final String url = "jdbc:mysql://localhost:3306/projet_symfony";
    private final String username = "root";
    private final String pwd = "";

    private Connection con;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            con = DriverManager.getConnection(url, username, pwd);
            System.out.println("✅ Connexion réussie à la base de données !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }

    // Singleton : une seule instance de la classe
    public static synchronized MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    // Retourne la connexion existante
    public Connection getCon() {
        try {
            // Vérifie si la connexion est toujours valide
            if (con == null || con.isClosed()) {
                System.out.println("⚠️ Reconnexion à la base de données...");
                con = DriverManager.getConnection(url, username, pwd);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la vérification de la connexion : " + e.getMessage());
        }
        return con;
    }

    // Méthode statique pour accéder facilement à la connexion
    public static Connection getConnection() {
        return getInstance().getCon();
    }

    // Version alternative de getCon() pour cohérence
    public Connection getCnx() {
        return getCon();
    }

    // Méthode pour fermer la connexion
    public static void closeConnection() {
        if (instance != null && instance.con != null) {
            try {
                instance.con.close();
                System.out.println("🔌 Connexion fermée avec succès");
                instance = null; // Permet de recréer une instance si besoin
            } catch (SQLException e) {
                System.out.println("❌ Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}