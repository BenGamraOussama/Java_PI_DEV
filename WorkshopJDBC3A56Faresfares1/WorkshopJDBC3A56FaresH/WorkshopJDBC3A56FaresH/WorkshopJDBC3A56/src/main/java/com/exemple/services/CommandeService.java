package com.exemple.services;

import com.exemple.entities.Commande;
import com.exemple.entities.Produit;
import com.exemple.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandeService implements IService<Commande> {

    private final Connection connection;

    public CommandeService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public boolean userExists(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public void ajouter(Commande commande) throws SQLException {
        // Vérifier si l'utilisateur existe
        if (!userExists(commande.getUserId())) {
            throw new SQLException("L'utilisateur avec l'ID " + commande.getUserId() + " n'existe pas");
        }

        String query = "INSERT INTO commande (date_commande, user_id, nom_client, prenom_client) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, Date.valueOf(commande.getDateCommande()));
            statement.setInt(2, commande.getUserId());
            statement.setString(3, commande.getNomClient());
            statement.setString(4, commande.getPrenomClient());
            
            statement.executeUpdate();
            
            // Récupérer l'ID généré
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    commande.setId(generatedKeys.getInt(1));
                }
            }
            
            // Ajouter les produits de la commande
            ajouterProduitsCommande(commande);
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void ajouterProduitsCommande(Commande commande) {
        String query = "INSERT INTO ligne_commande (commande_id, produit_id, quantite) VALUES (?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Map.Entry<Produit, Integer> entry : commande.getProduits().entrySet()) {
                statement.setInt(1, commande.getId());
                statement.setInt(2, entry.getKey().getId());
                statement.setInt(3, entry.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Commande commande) throws SQLException {
        String query = "UPDATE commande SET date_commande = ?, user_id = ?, nom_client = ?, prenom_client = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(commande.getDateCommande()));
            statement.setInt(2, commande.getUserId());
            statement.setString(3, commande.getNomClient());
            statement.setString(4, commande.getPrenomClient());
            statement.setInt(5, commande.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM commande WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Commande> afficher() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commande ORDER BY date_commande DESC";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                Commande commande = new Commande();
                commande.setId(resultSet.getInt("id"));
                commande.setDateCommande(resultSet.getDate("date_commande").toLocalDate());
                commande.setUserId(resultSet.getInt("user_id"));
                commande.setNomClient(resultSet.getString("nom_client"));
                commande.setPrenomClient(resultSet.getString("prenom_client"));
                commandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return commandes;
    }

    public List<Commande> rechercher(String id, String montant, String user) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM commande WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (!id.isEmpty()) {
            queryBuilder.append(" AND id = ?");
            parameters.add(Integer.parseInt(id));
        }
        if (!montant.isEmpty()) {
            try {
                double montantValue = Double.parseDouble(montant);
                queryBuilder.append(" AND montant_total >= ?");
                parameters.add(montantValue);
            } catch (NumberFormatException e) {
                // Si le montant n'est pas un nombre valide, on l'ignore
                System.out.println("Montant invalide : " + montant);
            }
        }
        if (!user.isEmpty()) {
            queryBuilder.append(" AND user_id = ?");
            parameters.add(Integer.parseInt(user));
        }

        queryBuilder.append(" ORDER BY date_commande DESC");

        try {
            PreparedStatement ps = connection.prepareStatement(queryBuilder.toString());
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Commande c = new Commande();
                c.setId(rs.getInt("id"));
                c.setDateCommande(rs.getDate("date_commande").toLocalDate());
                c.setMontantTotal(rs.getDouble("montant_total"));
                c.setUserId(rs.getInt("user_id"));
                commandes.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commandes;
    }
}
