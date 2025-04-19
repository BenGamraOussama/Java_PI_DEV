package org.example.pi__dev_.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Pidev {

    private final String url = "jdbc:mysql://localhost:3306/pi_dev";
    private final String username = "root";
    private final String pwd = "";

    private Connection con;

    public static Pidev instance;

    private Pidev(){
        try {
            // Charger explicitement le driver MySQL (utile parfois)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Se connecter à la base de données
            con = DriverManager.getConnection(url, username, pwd);
            System.out.println("connected!");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }

    public static Pidev getInstance(){
        if (instance == null)
            instance = new Pidev();
        return instance;
    }

    public Connection getCon() {
        return con;
    }
}
