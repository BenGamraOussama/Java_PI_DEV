package Services;

import Entities.Category;
import Utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CategoryService implements ICategory<Category> {
    private final Connection connection;

    public CategoryService() {
        connection = MyDatabase.getInstance().getConnection();
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

}
