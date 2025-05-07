package tn.esprit.pidev.gestion_activite.services;

import tn.esprit.pidev.gestion_activite.entities.Reponse;
import tn.esprit.pidev.Database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReponseService {
    private Connection connection;

    public ReponseService() {
        connection = Database.getConnection();
    }

    public void ajouter(Reponse r) throws SQLException {
        String sql = "INSERT INTO reponse (exercice_id, contenu) VALUES (?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, r.getExercice().getId());
        stmt.setString(2, r.getContenu());
        stmt.executeUpdate();
    }
    public List<Reponse> getByExerciceId(int exerciceId) throws SQLException {
        List<Reponse> reponses = new ArrayList<>();
        String req = "SELECT * FROM reponse WHERE exercice_id = ?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, exerciceId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Reponse r = new Reponse();
            r.setId(rs.getInt("id"));
            r.setContenu(rs.getString("contenu"));
            reponses.add(r);
        }
        return reponses;
    }

}