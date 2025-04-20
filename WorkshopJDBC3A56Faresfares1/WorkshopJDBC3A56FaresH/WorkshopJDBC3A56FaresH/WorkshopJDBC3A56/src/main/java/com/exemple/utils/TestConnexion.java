package com.exemple.utils;
import java.sql.Connection;
import java.sql.SQLException;


public class TestConnexion {

    public static void main(String[] args) {
        MyDatabase db = MyDatabase.getInstance();

        try (Connection connection = db.getConnection()) {
            if (connection != null) {
                System.out.println("✅ Connexion réussie !");
            } else {
                System.out.println("❌ Échec de la connexion !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }
}
