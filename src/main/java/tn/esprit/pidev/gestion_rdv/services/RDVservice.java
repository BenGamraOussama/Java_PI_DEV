package tn.esprit.pidev.gestion_rdv.services;


import tn.esprit.pidev.gestion_rdv.dao.DatabaseConnection;
import tn.esprit.pidev.gestion_rdv.enteties.Etat;
import tn.esprit.pidev.gestion_rdv.enteties.Patient;
import tn.esprit.pidev.gestion_rdv.enteties.Psychiatre;
import tn.esprit.pidev.gestion_rdv.enteties.RDV;
import tn.esprit.pidev.Database.Database;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RDVservice implements Iservice<RDV> {

    private Connection con;
    private boolean syncWithGoogleCalendar = true;

    public RDVservice(Connection con) throws SQLException {
        this.con = Database.getConnection();
    }

    public RDVservice(Connection con, boolean syncWithGoogleCalendar) throws SQLException {
        this.con = Database.getConnection();
        this.syncWithGoogleCalendar = syncWithGoogleCalendar;
    }

    public RDVservice() {

    }

    @Override
    public List<RDV> readList() throws SQLException {
        String query = "SELECT * FROM `rdv`";
        List<RDV> rdvs = new ArrayList<>();

        try (Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(query)) {

            while (rs.next()) {
                RDV r = new RDV(rs.getTime("heure"), rs.getDate("date"), rs.getString("priorite"));
                rdvs.add(r);
            }
        }
        return rdvs;
    }

    public void add(RDV rdv) throws SQLException {
        if (con == null) {
            System.out.println("La connexion à la base de données est nulle. Impossible d'ajouter le RDV.");
            return;  // Ou gérer l'erreur comme tu veux
        }

        String query = "INSERT INTO `rdv`(`heure`, `date`, `priorite`) VALUES (?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setTime(1, rdv.getHeure());
            pstmt.setDate(2, rdv.getDate());
            pstmt.setString(3, rdv.getPriorite());
            pstmt.executeUpdate();

            // Get the generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rdv.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("RDV ajouté avec succès !");

            // Sync with Google Calendar if enabled
            if (syncWithGoogleCalendar) {
                try {
                    addToGoogleCalendar(rdv);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la synchronisation avec Google Calendar: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Adds an appointment to Google Calendar
     * @param rdv The appointment to add
     * @throws IOException If there's an error communicating with Google Calendar
     */
    private void addToGoogleCalendar(RDV rdv) throws IOException {
        try {
            GoogleCalendarService.getCalendarService(); // Initialize the service
            GoogleCalendarService.createEvent(rdv);
            System.out.println("RDV ajouté à Google Calendar avec succès !");
        } catch (IOException e) {
            if (isCredentialsPlaceholderError(e)) {
                System.err.println("Les identifiants Google Calendar ne sont pas configurés. Veuillez mettre à jour le fichier credentials.json avec vos identifiants Google API.");
            } else {
                System.err.println("Erreur lors de l'ajout du RDV à Google Calendar: " + e.getMessage());
            }
            throw e;
        }
    }

    /**
     * Checks if an IOException is related to placeholder credentials
     * @param e The IOException to check
     * @return true if the error is related to placeholder credentials, false otherwise
     */
    private boolean isCredentialsPlaceholderError(IOException e) {
        return e.getMessage() != null && 
               (e.getMessage().contains("YOUR_CLIENT_ID") || 
                e.getMessage().contains("YOUR_CLIENT_SECRET"));
    }


    @Override
    public void update(RDV rdv) {
        String query = "UPDATE `rdv` SET `heure` = ?, `date` = ?, `priorite` = ? WHERE `id` = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setTime(1, rdv.getHeure());
            pstmt.setDate(2, rdv.getDate());
            pstmt.setString(3, rdv.getPriorite());
            pstmt.setInt(4, rdv.getId());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("RDV updated successfully.");

                // Sync with Google Calendar if enabled
                if (syncWithGoogleCalendar) {
                    try {
                        updateInGoogleCalendar(rdv);
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la mise à jour dans Google Calendar: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("No RDV found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an appointment in Google Calendar
     * @param rdv The appointment to update
     * @throws IOException If there's an error communicating with Google Calendar
     */
    private void updateInGoogleCalendar(RDV rdv) throws IOException {
        try {
            GoogleCalendarService.getCalendarService(); // Initialize the service
            String eventId = GoogleCalendarService.findEventIdByRdvId(rdv.getId());
            if (eventId != null) {
                GoogleCalendarService.updateEvent(eventId, rdv);
                System.out.println("RDV mis à jour dans Google Calendar avec succès !");
            } else {
                // Event not found, create a new one
                GoogleCalendarService.createEvent(rdv);
                System.out.println("RDV non trouvé dans Google Calendar, un nouveau a été créé !");
            }
        } catch (IOException e) {
            if (isCredentialsPlaceholderError(e)) {
                System.err.println("Les identifiants Google Calendar ne sont pas configurés. Veuillez mettre à jour le fichier credentials.json avec vos identifiants Google API.");
            } else {
                System.err.println("Erreur lors de la mise à jour du RDV dans Google Calendar: " + e.getMessage());
            }
            throw e;
        }
    }

    /**
     * Deletes an appointment and removes it from Google Calendar
     * @param id The ID of the appointment to delete
     * @throws SQLException If there's an error with the database
     * @throws IOException If there's an error communicating with Google Calendar
     */
    public void delete(int id) throws SQLException, IOException {
        // First get the RDV to have its details
        RDV rdv = getRDVById(id);
        if (rdv == null) {
            System.out.println("No RDV found with the given ID.");
            return;
        }

        // Delete from database
        String query = "DELETE FROM `rdv` WHERE `id` = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("RDV deleted successfully.");

                // Sync with Google Calendar if enabled
                if (syncWithGoogleCalendar) {
                    try {
                        deleteFromGoogleCalendar(id);
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la suppression dans Google Calendar: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("No RDV found with the given ID.");
            }
        }
    }

    /**
     * Deletes an appointment from Google Calendar
     * @param rdvId The ID of the appointment to delete
     * @throws IOException If there's an error communicating with Google Calendar
     */
    private void deleteFromGoogleCalendar(int rdvId) throws IOException {
        try {
            GoogleCalendarService.getCalendarService(); // Initialize the service
            String eventId = GoogleCalendarService.findEventIdByRdvId(rdvId);
            if (eventId != null) {
                GoogleCalendarService.deleteEvent(eventId);
                System.out.println("RDV supprimé de Google Calendar avec succès !");
            } else {
                System.out.println("RDV non trouvé dans Google Calendar.");
            }
        } catch (IOException e) {
            if (isCredentialsPlaceholderError(e)) {
                System.err.println("Les identifiants Google Calendar ne sont pas configurés. Veuillez mettre à jour le fichier credentials.json avec vos identifiants Google API.");
            } else {
                System.err.println("Erreur lors de la suppression du RDV de Google Calendar: " + e.getMessage());
            }
            throw e;
        }
    }



    public RDV getRDVById(int id) throws SQLException {
        String query = "SELECT * FROM rdv WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                RDV rdv = new RDV(
                        rs.getTime("heure"),
                        rs.getDate("date"),
                        rs.getString("priorite")
                );
                rdv.setId(rs.getInt("id"));
                return rdv;
            }
        }
        return null;
    }

    public List<RDV> getAllRDVs() throws SQLException {
        String query = "SELECT r.*, p.nom as patient_nom, psy.nom as psychiatre_nom " +
                "FROM rdv r " +
                "JOIN patient p ON r.patient_id = p.id " +
                "JOIN psychiatre psy ON r.psychiatre_id = psy.id";

        List<RDV> rdvs = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                RDV rdv = new RDV();
                rdv.setId(rs.getInt("id"));
                rdv.setDate(rs.getDate("date"));
                rdv.setHeure(rs.getTime("heure"));
                rdv.setPriorite(rs.getString("priorite"));

                Patient patient = new Patient();
                patient.setId(rs.getInt("patient_id"));
                patient.setFirstName(rs.getString("patient_nom"));
                patient.setLastName(rs.getString("patient_prenom"));
                rdv.setPatient(patient);

                Psychiatre psychiatre = new Psychiatre();
                psychiatre.setId(rs.getInt("psychiatre_id"));
                psychiatre.setFirstName(rs.getString("psychiatre_nom"));
                psychiatre.setLastName(rs.getString("psychiatre_prenom"));
                rdv.setPsychiatre(psychiatre);

                rdvs.add(rdv);
            }
        }
        return rdvs;
    }

    /**
     * Accepts an appointment by changing its state to VALIDEE
     * 
     * @param rdvId The ID of the appointment to accept
     * @return The updated RDV object if successful, null otherwise
     * @throws SQLException If there's an error with the database
     */
    public RDV acceptRDV(int rdvId) throws SQLException {
        RDV rdv = getRDVById(rdvId);
        if (rdv == null) {
            System.err.println("No RDV found with ID: " + rdvId);
            return null;
        }

        String query = "UPDATE `rdv` SET `etat` = ? WHERE `id` = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, Etat.VALIDEE.name());
            pstmt.setInt(2, rdvId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("RDV accepted successfully.");
                rdv.setEtat(Etat.VALIDEE);

                // Sync with Google Calendar if enabled
                if (syncWithGoogleCalendar) {
                    try {
                        updateInGoogleCalendar(rdv);
                    } catch (IOException e) {
                        System.err.println("Error updating Google Calendar: " + e.getMessage());
                    }
                }

                return rdv;
            } else {
                System.err.println("Failed to accept RDV with ID: " + rdvId);
                return null;
            }
        }
    }

    /**
     * Refuses an appointment by changing its state to ANNULEE
     * 
     * @param rdvId The ID of the appointment to refuse
     * @return The updated RDV object if successful, null otherwise
     * @throws SQLException If there's an error with the database
     */
    public RDV refuseRDV(int rdvId) throws SQLException {
        RDV rdv = getRDVById(rdvId);
        if (rdv == null) {
            System.err.println("No RDV found with ID: " + rdvId);
            return null;
        }

        String query = "UPDATE `rdv` SET `etat` = ? WHERE `id` = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, Etat.ANNULEE.name());
            pstmt.setInt(2, rdvId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("RDV refused successfully.");
                rdv.setEtat(Etat.ANNULEE);

                // Sync with Google Calendar if enabled
                if (syncWithGoogleCalendar) {
                    try {
                        // For refused appointments, we might want to delete them from Google Calendar
                        deleteFromGoogleCalendar(rdvId);
                    } catch (IOException e) {
                        System.err.println("Error updating Google Calendar: " + e.getMessage());
                    }
                }

                return rdv;
            } else {
                System.err.println("Failed to refuse RDV with ID: " + rdvId);
                return null;
            }
        }
    }
    public List<RDV> getAcceptedRDVsForPatient(int patientId) throws SQLException {
        List<RDV> acceptedRDVs = new ArrayList<>();
        String query = "SELECT * FROM `rdv` WHERE `patient_id` = ? AND `etat` = ? ORDER BY `date`, `heure`";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, patientId);
            pstmt.setString(2, Etat.VALIDEE.name());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RDV rdv = new RDV();
                rdv.setId(rs.getInt("id"));
                rdv.setHeure(rs.getTime("heure"));
                rdv.setDate(rs.getDate("date"));
                rdv.setPriorite(rs.getString("priorite"));
                rdv.setEtat(Etat.valueOf(rs.getString("etat")));

                acceptedRDVs.add(rdv);
            }
        }

        return acceptedRDVs;
    }

}
