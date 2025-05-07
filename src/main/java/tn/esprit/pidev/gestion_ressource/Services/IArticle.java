package tn.esprit.pidev.gestion_ressource.Services;

import java.sql.SQLException;
import java.util.List;

public interface IArticle<T> {
    void ajouter(T t) throws SQLException;
    void modifier(T t)throws SQLException;
    void supprimer(int id)throws SQLException;
    List<T> afficher() throws SQLException;
}
