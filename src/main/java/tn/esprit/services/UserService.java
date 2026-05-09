package tn.esprit.services;

import tn.esprit.entities.User;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserService — JDBC service for the `user` table.
 * Schema is shared with Symfony; field names match exactly.
 *
 * Status values (match Symfony): pending_verification, active, suspended, banned
 * Role values  (match Symfony):  user, admin, adminDestination, adminAccomodation,
 *                                adminOffers, adminBlog, adminTransport
 */
public class UserService {

    private Connection conx;

    public UserService() {
        conx = MyDB.getInstance().getConx();
    }

    private boolean checkConnection() {
        if (conx == null) {
            conx = MyDB.getInstance().getConx();
        }
        return conx != null;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Maps a ResultSet row to a fully-populated User object.
     * Reads every column that exists in the shared DB table.
     */
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User(
                rs.getInt("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("gender"),
                rs.getString("birth_year"),
                rs.getTimestamp("created_at")
        );

        // Nullable / optional columns — wrapped individually so a missing
        // column in an older schema does not crash the whole load.
        safeSetString(rs, "phone_number",    u::setPhoneNumber);
        safeSetString(rs, "role",            v -> u.setRole(v != null ? v : "user"));
        safeSetString(rs, "status",          v -> u.setStatus(v != null ? v : "pending_verification"));
        safeSetString(rs, "bio",             u::setBio);
        safeSetString(rs, "google_authenticator_secret", u::setGoogleAuthenticatorSecret);
        safeSetString(rs, "face_descriptor", u::setFaceDescriptor);

        // avatar_id is INT in Symfony
        try {
            int avatarId = rs.getInt("avatar_id");
            if (!rs.wasNull()) {
                u.setAvatarId(avatarId);
            }
        } catch (SQLException ignored) {}

        // email_verified TINYINT(1)
        try {
            u.setEmailVerified(rs.getBoolean("email_verified"));
        } catch (SQLException ignored) {}

        // dynamic_theme_enabled TINYINT(1)
        try {
            u.setDynamicThemeEnabled(rs.getBoolean("dynamic_theme_enabled"));
        } catch (SQLException ignored) {}

        // updated_at DATETIME
        try {
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                u.setUpdatedAt(updatedAt);
            }
        } catch (SQLException ignored) {}

        return u;
    }

    @FunctionalInterface
    private interface StringSetter { void set(String value); }

    private void safeSetString(ResultSet rs, String column, StringSetter setter) {
        try {
            setter.set(rs.getString(column));
        } catch (SQLException ignored) {}
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Create a new user (signup from Java side).
     * Sets status='pending_verification' and role='user' to match Symfony defaults.
     */
    public boolean createUser(User user) {
        if (!checkConnection()) return false;
        String sql = "INSERT INTO user (first_name, last_name, email, password, role, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole() != null ? user.getRole() : "user");
            ps.setString(6, user.getStatus() != null ? user.getStatus() : "pending_verification");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public User findByEmail(String email) {
        if (!checkConnection()) return null;
        try (PreparedStatement ps = conx.prepareStatement("SELECT * FROM user WHERE email = ?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }
        return null;
    }

    public User findById(int userId) {
        if (!checkConnection()) return null;
        try (PreparedStatement ps = conx.prepareStatement("SELECT * FROM user WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        if (!checkConnection()) return users;
        try (PreparedStatement ps = conx.prepareStatement("SELECT * FROM user ORDER BY user_id DESC")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    public String getRoleByEmail(String email) {
        if (!checkConnection()) return null;
        try (PreparedStatement ps = conx.prepareStatement("SELECT role FROM user WHERE email = ?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("role");
        } catch (SQLException e) {
            System.err.println("Error getting role: " + e.getMessage());
        }
        return null;
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────────

    /**
     * Simple login check (plain-text comparison for Java-created accounts).
     * Symfony accounts use bcrypt — cannot be verified here; use Symfony for those.
     */
    public boolean login(String email, String password) {
        User user = findByEmail(email);
        return user != null && user.getPassword() != null && user.getPassword().equals(password);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * General user update — updates all profile fields + updated_at timestamp.
     */
    public boolean updateUser(User user) {
        if (!checkConnection()) return false;
        String sql = "UPDATE user SET " +
                     "first_name = ?, last_name = ?, email = ?, phone_number = ?, " +
                     "gender = ?, birth_year = ?, bio = ?, role = ?, updated_at = NOW() " +
                     "WHERE user_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getGender());
            ps.setString(6, user.getBirthYear());
            ps.setString(7, user.getBio());
            ps.setString(8, user.getRole() != null ? user.getRole() : "user");
            ps.setInt(9, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserPassword(User user) {
        if (!checkConnection()) return false;
        String sql = "UPDATE user SET password = ?, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setString(1, user.getPassword());
            ps.setInt(2, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserDemographics(User user) {
        if (!checkConnection()) return false;
        String sql = "UPDATE user SET gender = ?, birth_year = ?, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setString(1, user.getGender());
            ps.setString(2, user.getBirthYear());
            ps.setInt(3, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating demographics: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update avatar_id — Symfony stores this as INT.
     * Accepts Integer (null = clear avatar).
     */
    public boolean updateUserAvatar(int userId, Integer avatarId) {
        if (!checkConnection()) return false;
        String sql = "UPDATE user SET avatar_id = ?, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            if (avatarId != null) {
                ps.setInt(1, avatarId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating avatar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Legacy overload — accepts String avatarId for backwards compatibility.
     * Converts to Integer if possible.
     */
    public boolean updateUserAvatar(int userId, String avatarId) {
        Integer id = null;
        if (avatarId != null && !avatarId.isEmpty()) {
            try { id = Integer.parseInt(avatarId); } catch (NumberFormatException ignored) {}
        }
        return updateUserAvatar(userId, id);
    }

    /**
     * Update user status.
     * Valid values (match Symfony): pending_verification, active, suspended, banned
     */
    public boolean updateUserStatus(int userId, String status) {
        if (!checkConnection()) return false;
        String sql = "UPDATE user SET status = ?, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating status: " + e.getMessage());
            return false;
        }
    }

    // ── EMAIL VERIFICATION ────────────────────────────────────────────────────

    /**
     * Mark the user's email as verified and set their status to 'active'.
     * Called after the user enters the correct OTP code in EmailVerificationController.
     * This updates the shared DB — Symfony will immediately see the user as verified.
     */
    public boolean markEmailVerified(int userId) {
        if (!checkConnection()) return false;
        String sql = "UPDATE user SET email_verified = 1, status = 'active', updated_at = NOW() " +
                     "WHERE user_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error marking email verified: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deleteUser(int userId) {
        if (!checkConnection()) return false;
        String sql = "DELETE FROM user WHERE user_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
}
