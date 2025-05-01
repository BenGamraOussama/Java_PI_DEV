package tn.esprit.pidev.Service;
import tn.esprit.pidev.Database.Database;
import tn.esprit.pidev.Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserDAO {
    private Connection connection;
    private PreparedStatement pst;
    private ResultSet rs;

    public UserDAO() {
        connection = Database.getConnection();
    }

    /**
     * Hashes a password using BCrypt
     * @param plainPassword The plain text password to hash
     * @return The hashed password
     */
    private String hashPassword(String plainPassword) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        // Symfony utilise $2y$ au lieu de $2a$ pour BCrypt standard
        String encoded = encoder.encode(plainPassword);
        return "$2y$" + encoded.substring(4); // Conversion vers le format Symfony
    }

    /**
     * Checks if a plain password matches a Symfony hashed password
     */
    private boolean checkPassword(String plainPassword, String symfonyHash) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        // Adaptation pour le format Symfony
        if (symfonyHash.startsWith("$2y$")) {
            // Convertit le format $2y$ de Symfony en $2a$ pour BCrypt
            String convertedHash = "$2a$" + symfonyHash.substring(4);
            return encoder.matches(plainPassword, convertedHash);
        }
        return encoder.matches(plainPassword, symfonyHash);
    }

    // Add these methods to your UserDAO class

    /**
     * Checks if an email exists in the database
     * @param email The email to check
     * @return True if the email exists, false otherwise
     */
    public boolean checkEmailExists(String email) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT COUNT(*) FROM user WHERE email = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, email);

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public boolean updateProfile(User user) {
        String query = "UPDATE user SET first_name = ?, last_name = ?, adresse = ?, phone = ?, specialite = ? WHERE id = ?";

        try {
            pst = connection.prepareStatement(query);
            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(3, user.getAddress());
            pst.setString(4, user.getPhoneNumber());
            pst.setString(5, user.getSpecialite());
            pst.setInt(6, user.getId());

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating profile: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    /**
     * Updates a user's password in the database
     * @param email The user's email
     * @param newPassword The new password
     * @return True if the update was successful, false otherwise
     */
    public boolean updatePassword(String email, String newPassword) {
        PreparedStatement ps = null;

        try {
            // Hash the password before storing
            String hashedPassword = hashPassword(newPassword);

            // Use the existing connection from the constructor
            String query = "UPDATE user SET password = ? WHERE email = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, hashedPassword); // Store the hashed password
            ps.setString(2, email);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Only close the PreparedStatement, not the connection
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public boolean addUser(User user) {
        // Première requête pour insérer dans la table user
        String userQuery = "INSERT INTO user (email, password, first_name, last_name, roles, specialite, adresse, birth_date, phone, discr) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Requête pour insérer dans la table patient (si c'est un patient)
        String patientQuery = "INSERT INTO patient (id, adresse, name) VALUES (?, ?, ?)";

        try {
            // Hash du mot de passe
            String hashedPassword = hashPassword(user.getPassword());

            // Déterminer le discr
            String roles = String.join(", ", user.getRole());
            String discr = "utilisateur";

            if (roles.contains("ROLE_PATIENT")) {
                discr = "patient";
            } else if (roles.contains("ROLE_PSYCHIATRE")) {
                discr = "psychiatre";
            } else if (roles.contains("ROLE_FOURNISSEUR")) {
                discr = "fournisseur";
            }

            // Insertion dans la table user
            pst = connection.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, user.getEmail());
            pst.setString(2, hashedPassword);
            pst.setString(3, user.getFirstName());
            pst.setString(4, user.getLastName());
            pst.setString(5, roles);
            pst.setString(6, user.getSpecialite());
            pst.setString(7, user.getAddress());
            pst.setDate(8, user.getBirthDate());
            pst.setString(9, user.getPhoneNumber());
            pst.setString(10, discr);

            int rowsAffected = pst.executeUpdate();

            // Si c'est un patient et que l'insertion dans user a réussi
            if (rowsAffected > 0 && discr.equals("patient")) {
                // Récupérer l'ID généré
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long userId = generatedKeys.getLong(1);

                    // Insertion dans la table patient
                    PreparedStatement pstPatient = connection.prepareStatement(patientQuery);
                    pstPatient.setLong(1, userId);
                    pstPatient.setString(2, user.getAddress()); // Remplacez par le champ supplémentaire du patient
                    pstPatient.setString(3, user.getAddress());

                    int patientRowsAffected = pstPatient.executeUpdate();
                    if (patientRowsAffected <= 0) {
                        // Rollback si l'insertion dans patient échoue ?
                        return false;
                    }
                }
            }

            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error adding user: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }
    public User authenticateUser(String email, String password) {
        String query = "SELECT * FROM user WHERE email = ?";

        try {
            pst = connection.prepareStatement(query);
            pst.setString(1, email);

            rs = pst.executeQuery();

            if (rs.next()) {
                // Get the stored hashed password
                String storedHash = rs.getString("password");

                // Check if user is banned
                boolean banned = false;
                try {
                    banned = rs.getBoolean("banned");
                } catch (SQLException e) {
                    // Column doesn't exist, default to false
                    banned = false;
                }

                // If user is banned, return null with a specific message
                if (banned) {
                    System.out.println("User is banned and cannot log in: " + email);
                    return null;
                }

                // Check if the provided password matches the stored hash
                if (checkPassword(password, storedHash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(storedHash); // Store the hash, not the plain password
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    String rolesStr = rs.getString("roles");
                    user.setRole(rolesStr != null ? rolesStr.split(",") : new String[0]);
                    user.setSpecialite(rs.getString("specialite"));
                    user.setAddress(rs.getString("adresse"));
                    user.setBirthDate(rs.getDate("birth_date"));
                    user.setPhoneNumber(rs.getString("phone"));
                    user.setBanned(banned);

                    // Set the static connected user
                    User.connecte = user;

                    return user;
                }
            }
            return null;
        } catch (SQLException ex) {
            System.out.println("Error authenticating: " + ex.getMessage());
            return null;
        } finally {
            closeResources();
        }
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        String query = "UPDATE user SET password = ? WHERE id = ?";

        try {
            // Hash the new password
            String hashedPassword = hashPassword(newPassword);

            pst = connection.prepareStatement(query);
            pst.setString(1, hashedPassword);
            pst.setInt(2, userId);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating password: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";

        try {
            pst = connection.prepareStatement(query);
            pst.setString(1, email);

            rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException ex) {
            System.out.println("Error checking email: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    public List<User> getAllAdmin() {
        List<User> doctors = new ArrayList<>();
        String query = "SELECT * FROM user WHERE roles = '[\"ROLE_ADMIN\"]'";

        try {
            pst = connection.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                User doctor = new User();
                doctor.setId(rs.getInt("id"));
                doctor.setEmail(rs.getString("email"));
                doctor.setFirstName(rs.getString("first_name"));
                doctor.setLastName(rs.getString("last_name"));
                String rolesStr = rs.getString("roles");
                doctor.setRole(rolesStr != null ? rolesStr.split(",") : new String[0]);
                doctor.setSpecialite(rs.getString("specialite"));

                doctors.add(doctor);
            }
            return doctors;
        } catch (SQLException ex) {
            System.out.println("Error getting doctors: " + ex.getMessage());
            return doctors;
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException ex) {
            System.out.println("Error closing resources: " + ex.getMessage());
        }
    }


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";

        try {
            pst = connection.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                String rolesStr = rs.getString("roles");
                boolean banned = false;
                // Check if banned column exists
                try {
                    banned = rs.getBoolean("banned");
                } catch (SQLException e) {
                    // Column doesn't exist, default to false
                    banned = false;
                }

                User user = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        "", // On ne récupère pas le mot de passe
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rolesStr != null ? rolesStr.split(",") : new String[0],
                        rs.getString("specialite"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("adresse"),
                        rs.getDate("birth_date"),
                        rs.getString("phone")
                );
                user.setBanned(banned);
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting users: " + ex.getMessage());
        } finally {
            closeResources();
        }
        return users;
    }

    public boolean updateUser(User user) {
        String query = "UPDATE user SET first_name=?, last_name=?, roles=?, phone=?, email=? WHERE id=?";

        try {
            pst = connection.prepareStatement(query);
            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(3, String.join(",", user.getRole()));
            pst.setString(4, user.getPhoneNumber());
            pst.setString(5, user.getEmail());
            pst.setInt(6, user.getId());

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating user: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean deleteUser(int id) {
        String query = "DELETE FROM user WHERE id=?";

        try {
            pst = connection.prepareStatement(query);
            pst.setInt(1, id);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error deleting user: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }
    public User getUserById(int userId) {
        String query = "SELECT * FROM user WHERE id = ?";
        User user = null;

        try {
            // Prepare the statement and set the parameter
            pst = connection.prepareStatement(query);
            pst.setInt(1, userId); // Bind the userId parameter
            rs = pst.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone"));
                String rolesStr = rs.getString("roles");
                user.setRole(rolesStr != null ? rolesStr.split(",") : new String[0]);

                // Check if banned column exists
                try {
                    user.setBanned(rs.getBoolean("banned"));
                } catch (SQLException e) {
                    // Column doesn't exist, default to false
                    user.setBanned(false);
                }
            } else {
                System.err.println("Aucun utilisateur trouvé avec l'ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
        } finally {
            // Close resources properly
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                // Do not close the connection here if it is reused elsewhere
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    /**
     * Ban a user by setting their banned status to true
     * @param userId The ID of the user to ban
     * @return True if the ban was successful, false otherwise
     */
    public boolean banUser(int userId) {
        String query = "UPDATE user SET banned = ? WHERE id = ?";

        try {
            // First check if the banned column exists
            try {
                // Try to get a user to check if banned column exists
                String checkQuery = "SELECT banned FROM user LIMIT 1";
                PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
                checkStmt.executeQuery();
                checkStmt.close();
            } catch (SQLException e) {
                // Column doesn't exist, create it
                String alterQuery = "ALTER TABLE user ADD COLUMN banned BOOLEAN DEFAULT FALSE";
                PreparedStatement alterStmt = connection.prepareStatement(alterQuery);
                alterStmt.executeUpdate();
                alterStmt.close();
            }

            // Now update the user's banned status
            pst = connection.prepareStatement(query);
            pst.setBoolean(1, true);
            pst.setInt(2, userId);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error banning user: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    /**
     * Unban a user by setting their banned status to false
     * @param userId The ID of the user to unban
     * @return True if the unban was successful, false otherwise
     */
    public boolean unbanUser(int userId) {
        String query = "UPDATE user SET banned = ? WHERE id = ?";

        try {
            pst = connection.prepareStatement(query);
            pst.setBoolean(1, false);
            pst.setInt(2, userId);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error unbanning user: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    /**
     * Check if a user is banned by email
     * @param email The email of the user to check
     * @return True if the user is banned, false otherwise
     */
    public boolean isUserBanned(String email) {
        String query = "SELECT banned FROM user WHERE email = ?";

        try {
            // First check if the banned column exists
            try {
                // Try to get a user to check if banned column exists
                String checkQuery = "SELECT banned FROM user LIMIT 1";
                PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
                checkStmt.executeQuery();
                checkStmt.close();
            } catch (SQLException e) {
                // Column doesn't exist, create it
                String alterQuery = "ALTER TABLE user ADD COLUMN banned BOOLEAN DEFAULT FALSE";
                PreparedStatement alterStmt = connection.prepareStatement(alterQuery);
                alterStmt.executeUpdate();
                alterStmt.close();
                return false; // If column didn't exist, user can't be banned
            }

            // Now check if the user is banned
            pst = connection.prepareStatement(query);
            pst.setString(1, email);

            rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("banned");
            }
            return false; // User not found
        } catch (SQLException ex) {
            System.out.println("Error checking if user is banned: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    /**
     * Get the count of users by role
     * @return A map with role as key and count as value
     */
    public Map<String, Integer> getUserCountByRole() {
        Map<String, Integer> roleCounts = new HashMap<>();
        List<User> users = getAllUsers();

        for (User user : users) {
            String[] roles = user.getRole();
            if (roles != null) {
                for (String role : roles) {
                    // Trim the role to remove any whitespace
                    role = role.trim();
                    // Increment the count for this role
                    roleCounts.put(role, roleCounts.getOrDefault(role, 0) + 1);
                }
            }
        }

        return roleCounts;
    }

    /**
     * Get the count of banned users
     * @return The number of banned users
     */
    public int getBannedUserCount() {
        int count = 0;
        List<User> users = getAllUsers();

        for (User user : users) {
            if (user.isBanned()) {
                count++;
            }
        }

        return count;
    }
}
