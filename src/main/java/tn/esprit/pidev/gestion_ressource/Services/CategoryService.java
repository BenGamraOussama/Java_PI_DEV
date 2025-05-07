package tn.esprit.pidev.gestion_ressource.Services;

import tn.esprit.pidev.gestion_ressource.Entities.Article;
import tn.esprit.pidev.gestion_ressource.Entities.Category;
import tn.esprit.pidev.Database.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CategoryService implements ICategory<Category> {
    private final Connection connection;

    public CategoryService() {
        connection = Database.getConnection();
    }

    @Override
    public void ajouter(Category c) throws SQLException {
        String query = "INSERT INTO category (name, description, image, created_at) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, c.getName());
        ps.setString(2, c.getDescription());
        ps.setString(3, c.getImage());
        ps.setDate(4, Date.valueOf(c.getCreatedAt()));
        ps.executeUpdate();
    }

    @Override
    public void modifier(Category c) throws SQLException {
        String query = "UPDATE category SET name=?, description=?, image=?, created_at=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, c.getName());
        ps.setString(2, c.getDescription());
        ps.setString(3, c.getImage());
        ps.setDate(4, Date.valueOf(c.getCreatedAt()));
        ps.setInt(5, c.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM category WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Category> afficher() throws SQLException {
        List<Category> list = new ArrayList<>();
        String query = "SELECT * FROM category";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            Category c = new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("image"),
                    rs.getDate("created_at").toLocalDate()
            );
            list.add(c);
        }
        return list;
    }

    public Category getById(int id) throws SQLException {
        String query = "SELECT * FROM category WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("image"),
                    rs.getDate("created_at").toLocalDate()
            );
        }
        return null;
    }
    public List<Category> rechercher(String keyword) throws SQLException {
        List<Category> result = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE name LIKE ? OR description LIKE ?";

        try{
            PreparedStatement stmt = connection.prepareStatement(sql);
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setImage(rs.getString("image"));
                category.setCreatedAt(rs.getObject("created_At", LocalDate.class));

                result.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}
