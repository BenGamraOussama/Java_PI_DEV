package tn.esprit.pidev.gestion_commande.services;

import tn.esprit.pidev.gestion_commande.entities.Commande;
import tn.esprit.pidev.gestion_commande.entities.Produit;
import tn.esprit.pidev.Database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandeService implements IService<Commande> {

    private final Connection connection;

    public CommandeService() {
        connection = Database.getConnection();
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

    public List<Commande> afficherTriDate(boolean ascending) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String query = """
            SELECT c.*, GROUP_CONCAT(CONCAT(p.id, ':', lc.quantite) SEPARATOR ';') as produits_info
            FROM commande c
            LEFT JOIN ligne_commande lc ON c.id = lc.commande_id
            LEFT JOIN produit p ON lc.produit_id = p.id
            GROUP BY c.id
            ORDER BY c.date_commande """ + (ascending ? "ASC" : "DESC");

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            
            while (rs.next()) {
                Commande commande = new Commande();
                commande.setId(rs.getInt("id"));
                commande.setDateCommande(rs.getDate("date_commande").toLocalDate());
                commande.setUserId(rs.getInt("user_id"));
                commande.setNomClient(rs.getString("nom_client"));
                commande.setPrenomClient(rs.getString("prenom_client"));
                
                // Process products if any
                String produitsInfo = rs.getString("produits_info");
                if (produitsInfo != null && !produitsInfo.isEmpty()) {
                    Map<Produit, Integer> produits = new HashMap<>();
                    for (String produitInfo : produitsInfo.split(";")) {
                        String[] parts = produitInfo.split(":");
                        if (parts.length == 2) {
                            Produit produit = new Produit();
                            produit.setId(Integer.parseInt(parts[0]));
                            produits.put(produit, Integer.parseInt(parts[1]));
                        }
                    }
                    commande.setProduits(produits);
                }
                
                commandes.add(commande);
            }
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

    public Map<String, Double> getProductSalesStatistics() throws SQLException {
        Map<String, Double> statistics = new LinkedHashMap<>();
        String query = """
            SELECT p.nom, COUNT(*) * 100.0 / (SELECT COUNT(*) FROM ligne_commande) as percentage
            FROM ligne_commande lc
            JOIN produit p ON lc.produit_id = p.id
            GROUP BY p.id, p.nom
            ORDER BY percentage DESC
        """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String productName = rs.getString("nom");
                double percentage = rs.getDouble("percentage");
                statistics.put(productName, Math.round(percentage * 100.0) / 100.0); // Round to 2 decimal places
            }
        }
        return statistics;
    }

    public Map<String, Double> getCommandeStatistics() throws SQLException {
        Map<String, Double> statistics = new LinkedHashMap<>();
        
        // Get total number of orders per client
        String clientOrdersQuery = """
            SELECT CONCAT(nom_client, ' ', prenom_client) as client_name, 
                   COUNT(*) * 100.0 / (SELECT COUNT(*) FROM commande) as percentage
            FROM commande 
            GROUP BY nom_client, prenom_client
            ORDER BY percentage DESC
            LIMIT 5
        """;
        
        // Get orders by month
        String monthlyOrdersQuery = """
            SELECT DATE_FORMAT(date_commande, '%M %Y') as month_year,
                   COUNT(*) * 100.0 / (SELECT COUNT(*) FROM commande) as percentage
            FROM commande
            GROUP BY month_year
            ORDER BY date_commande DESC
            LIMIT 6
        """;
        
        try (Statement stmt = connection.createStatement()) {
            // Get client statistics
            try (ResultSet rs = stmt.executeQuery(clientOrdersQuery)) {
                while (rs.next()) {
                    String clientName = rs.getString("client_name");
                    double percentage = rs.getDouble("percentage");
                    statistics.put("Client: " + clientName, Math.round(percentage * 100.0) / 100.0);
                }
            }
            
            // Get monthly statistics
            try (ResultSet rs = stmt.executeQuery(monthlyOrdersQuery)) {
                while (rs.next()) {
                    String monthYear = rs.getString("month_year");
                    double percentage = rs.getDouble("percentage");
                    statistics.put("Mois: " + monthYear, Math.round(percentage * 100.0) / 100.0);
                }
            }
        }
        return statistics;
    }
}
