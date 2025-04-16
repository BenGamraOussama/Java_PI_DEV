package pii.services;

import pii.entities.Article;

import java.sql.SQLException;
import java.util.List;

public class ArticleService implements IService<Article> {
    @Override
    public void ajouter(Article p) throws SQLException {
        
    }

    @Override
    public void modifier(Article p) throws SQLException {

    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    @Override
    public Article getOneById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Article> getAll() throws SQLException {
        return List.of();
    }
}
