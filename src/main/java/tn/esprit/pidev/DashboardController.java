package tn.esprit.pidev;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserDAO;

import org.kordamp.ikonli.javafx.FontIcon;
import java.util.Map;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // Composants du dashboard
    @FXML private Label welcomeLabel;
    @FXML private Label specialityLabel;
    @FXML private Text todayAppointmentsCount;
    @FXML private Text totalPatientsCount;
    @FXML private Text pendingReportsCount;
    @FXML private TableView<?> upcomingAppointmentsTable;
    @FXML private ListView<?> notificationsListView;

    // Chart components for user statistics
    @FXML private PieChart userRoleChart;
    @FXML private BarChart<String, Number> userStatusChart;

    // Panneaux de contenu
    @FXML private VBox dashboardPane;
    @FXML private VBox appointmentsPane;
    @FXML private VBox patientRecordsPane;
    @FXML private VBox prescriptionsPane;
    @FXML private VBox medicalNotesPane;
    @FXML private VBox userManagementPane;
    @FXML private VBox ActivitePane;
    @FXML private VBox CommandePane;

    @FXML private VBox ArticlePane;
    @FXML private VBox CategoryPane;

    @FXML private VBox CategoryProduitPane;
    @FXML private VBox ProduitPane;



    @FXML private VBox profilePane;
    @FXML private VBox settingsPane;

    @FXML
    private VBox sidebar;

    @FXML
    private Button openSidebarBtn;

    @FXML
    private Button closeSidebarBtn;

    @FXML
    private ImageView myImageView;

    // Contrôleur pour la gestion des utilisateurs
    private UserManagementController userManagementController;

    // UserDAO instance for database operations
    private UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialisation avec l'utilisateur connecté
        if (User.connecte != null) {
            welcomeLabel.setText("Welcome, " + User.connecte.getLastName());
        }

        // Initialisation du panneau de gestion des utilisateurs
        initUserManagementPane();

        // Initialize user statistics charts
        initUserStatisticsCharts();
    }

    /**
     * Initialize the user statistics charts with data from the database
     */
    private void initUserStatisticsCharts() {
        try {
            // Get user statistics from the database
            Map<String, Integer> userRoleCounts = userDAO.getUserCountByRole();
            int bannedUserCount = userDAO.getBannedUserCount();

            // Create data for the pie chart (user roles)
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : userRoleCounts.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

            // Set the data to the pie chart
            if (userRoleChart != null) {
                userRoleChart.setData(pieChartData);
                userRoleChart.setTitle("Users by Role");
            }

            // Create data for the bar chart (banned vs active users)
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("User Status");

            // Calculate active users (total - banned)
            int totalUsers = 0;
            for (Integer count : userRoleCounts.values()) {
                totalUsers += count;
            }
            int activeUsers = totalUsers - bannedUserCount;

            series.getData().add(new XYChart.Data<>("Active", activeUsers));
            series.getData().add(new XYChart.Data<>("Banned", bannedUserCount));

            // Set the data to the bar chart
            if (userStatusChart != null) {
                userStatusChart.getData().clear();
                userStatusChart.getData().add(series);
                userStatusChart.setTitle("User Status");
            }
        } catch (Exception e) {
            System.err.println("Error initializing user statistics charts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initUserManagementPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserManagement.fxml"));
            Node userManagementView = loader.load();
            userManagementController = loader.getController();

            // Ajout unique de la vue au panneau
            userManagementPane.getChildren().setAll(userManagementView);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de UserManagement.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    public void toggleSidebar() {
        boolean isVisible = sidebar.isVisible();
        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible);
        openSidebarBtn.setVisible(isVisible);
        closeSidebarBtn.setVisible(!isVisible);
    }

    private void hideAllPanes() {
        dashboardPane.setVisible(false);
        appointmentsPane.setVisible(false);
        patientRecordsPane.setVisible(false);
        prescriptionsPane.setVisible(false);
        medicalNotesPane.setVisible(false);
        userManagementPane.setVisible(false);
        ActivitePane.setVisible(false);
        CommandePane.setVisible(false);
        ArticlePane.setVisible(false);
        CategoryPane.setVisible(false);
        CategoryProduitPane.setVisible(false);
        ProduitPane.setVisible(false);
        profilePane.setVisible(false);
        settingsPane.setVisible(false);
    }



    // Méthodes de navigation
    @FXML
    private void showDashboard(ActionEvent event) {
        hideAllPanes();
        dashboardPane.setVisible(true);

        // Refresh the user statistics charts when showing dashboard
        initUserStatisticsCharts();
    }

    @FXML
    private void showAppointments(ActionEvent event) {
        hideAllPanes();
        appointmentsPane.setVisible(true);
    }

    @FXML
    private void showPatientRecords(ActionEvent event) {
        hideAllPanes();
        patientRecordsPane.setVisible(true);
    }

    @FXML
    private void showPrescriptions(ActionEvent event) {
        hideAllPanes();
        prescriptionsPane.setVisible(true);
    }

    @FXML
    private void showMedicalNotes(ActionEvent event) {
        hideAllPanes();
        medicalNotesPane.setVisible(true);
    }

    @FXML
    private void showUserManagement(ActionEvent event) {
        hideAllPanes();
        userManagementPane.setVisible(true);
        userManagementController.loadUsers(); // Rafraîchit la liste des utilisateurs

        // Refresh the user statistics charts when showing user management
        initUserStatisticsCharts();
    }

    @FXML
    private void showActivite(ActionEvent event) {
        hideAllPanes();
        ActivitePane.setVisible(true);
    }
    @FXML
    private void showCommande(ActionEvent event) {
        hideAllPanes();
        CommandePane.setVisible(true);
    }
    @FXML
    private void showArticle(ActionEvent event) {
        hideAllPanes();
        ArticlePane.setVisible(true);
    }
    @FXML
    private void showCategory(ActionEvent event) {
        hideAllPanes();
        CategoryPane.setVisible(true);
    }
    @FXML
    private void showCategoryProduit(ActionEvent event) {
        hideAllPanes();
        CategoryProduitPane.setVisible(true);
    }
    @FXML
    private void showProduit(ActionEvent event) {
        hideAllPanes();
        ProduitPane.setVisible(true);
    }

    @FXML
    private void showProfile(ActionEvent event) {
        hideAllPanes();
        profilePane.setVisible(true);
    }

    @FXML
    private void showSettings(ActionEvent event) {
        hideAllPanes();
        settingsPane.setVisible(true);
    }

    // Méthodes des actions rapides
    @FXML
    private void createNewAppointment(ActionEvent event) {
        showAlert("Création de rendez-vous", "Fonctionnalité à implémenter");
    }

    @FXML
    private void writeNewPrescription(ActionEvent event) {
        showAlert("Nouvelle ordonnance", "Fonctionnalité à implémenter");
    }

    @FXML
    private void addMedicalNote(ActionEvent event) {
        showAlert("Ajout de note médicale", "Fonctionnalité à implémenter");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            User.connecte = null; // Réinitialisation de l'utilisateur connecté

            // Chargement de la vue de login
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/pidev/Login.fxml"));
            Scene scene = new Scene(root);

            // Obtention de la fenêtre actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changement de scène
            stage.setScene(scene);
            stage.setTitle("HopeNest / Login");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
