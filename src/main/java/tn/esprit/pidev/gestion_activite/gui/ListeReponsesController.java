package tn.esprit.pidev.gestion_activite.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import tn.esprit.pidev.gestion_activite.entities.Exercice;
import tn.esprit.pidev.gestion_activite.entities.Reponse;
import tn.esprit.pidev.gestion_activite.services.ReponseService;

import java.sql.SQLException;
import java.util.List;

public class ListeReponsesController {

    @FXML
    private Label questionLabel;

    @FXML
    private ListView<String> reponsesList;

    private Exercice exercice;

    public void setExercice(Exercice exercice) {
        this.exercice = exercice;
        questionLabel.setText("Question: " + exercice.getQuestion());
        chargerReponses();
    }

    private void chargerReponses() {
        ReponseService rs = new ReponseService();
        List<Reponse> reponses = null;
        try {
            reponses = rs.getByExerciceId(exercice.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        reponsesList.getItems().clear();
        for (Reponse r : reponses) {
            reponsesList.getItems().add(r.getContenu());
        }
    }
}
