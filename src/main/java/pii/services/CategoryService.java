package pii.services;

import pii.entities.Category;

import java.sql.SQLException;
import java.util.List;

public class CategoryService implements IService<Category> {
    @Override
    public void ajouter(Category p) throws SQLException {
        
    }

    @Override
    public void modifier(Category p) throws SQLException {

    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    @Override
    public Category getOneById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Category> getAll() throws SQLException {
        return List.of();
    }
}
