package tn.esprit.pidev.Service;
import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.pidev.Database.Database;
import tn.esprit.pidev.Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Checks if a plain password matches a BCrypt hashed password
     * @param plainPassword The plain password to check
     * @param hashedPassword The hashed password to check against
     * @return True if the password matches, false otherwise
     */
    private boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
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
        String query = "INSERT INTO user (email, password, first_name, last_name, role, specialite, address, birth_date, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Hash the password before storing
            String hashedPassword = hashPassword(user.getPassword());

            pst = connection.prepareStatement(query);
            pst.setString(1, user.getEmail());
            pst.setString(2, hashedPassword); // Store the hashed password
            pst.setString(3, user.getFirstName());
            pst.setString(4, user.getLastName());
            pst.setString(5, String.join(",", user.getRole()));
            pst.setString(6, user.getSpecialite());
            pst.setString(7, user.getAddress());
            pst.setDate(8, user.getBirthDate());
            pst.setString(9, user.getPhoneNumber());

            int rowsAffected = pst.executeUpdate();
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

                // Check if the provided password matches the stored hash
                if (checkPassword(password, storedHash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(storedHash); // Store the hash, not the plain password
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    String rolesStr = rs.getString("role");
                    user.setRole(rolesStr != null ? rolesStr.split(",") : new String[0]);
                    user.setSpecialite(rs.getString("specialite"));
                    user.setAddress(rs.getString("address"));
                    user.setBirthDate(rs.getDate("birth_date"));
                    user.setPhoneNumber(rs.getString("phone_number"));

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
        String query = "SELECT * FROM user WHERE role = 'admin'";

        try {
            pst = connection.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                User doctor = new User();
                doctor.setId(rs.getInt("id"));
                doctor.setEmail(rs.getString("email"));
                doctor.setFirstName(rs.getString("first_name"));
                doctor.setLastName(rs.getString("last_name"));
                String rolesStr = rs.getString("role");
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
                String rolesStr = rs.getString("role");
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        "", // On ne récupère pas le mot de passe
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rolesStr != null ? rolesStr.split(",") : new String[0],
                        rs.getString("specialite"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getDate("birth_date"),
                        rs.getString("phone_number")
                );
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
        String query = "UPDATE user SET first_name=?, last_name=?, role=?, phone_number=?, email=? WHERE id=?";

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
                user.setPhoneNumber(rs.getString("phone_number"));
                String rolesStr = rs.getString("role");
                user.setRole(rolesStr != null ? rolesStr.split(",") : new String[0]);
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
    }}