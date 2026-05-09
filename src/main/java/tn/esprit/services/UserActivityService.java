package tn.esprit.services;

import tn.esprit.entities.UserActivity;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserActivityService — JDBC service for the `user_activity_log` table.
 * Schema shared with Symfony UserActivityLog entity.
 *
 * Table columns: id, user_id, activity_type, target_id (VARCHAR 255), target_type, timestamp
 */
public class UserActivityService {

    private Connection conx;

    public UserActivityService() {
        conx = MyDB.getInstance().getConx();
        if (conx == null) {
            System.err.println("❌ UserActivityService: Connection is null!");
        }
    }

    private boolean checkConnection() {
        if (conx == null) conx = MyDB.getInstance().getConx();
        return conx != null;
    }

    /**
     * Insert a new activity log entry.
     * target_id is stored as VARCHAR in Symfony — we send it as String.
     */
    public boolean logActivity(UserActivity activity) {
        if (!checkConnection()) return false;
        String sql = "INSERT INTO user_activity_log (user_id, activity_type, target_id, target_type, timestamp) " +
                     "VALUES (?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, activity.getUserId());
            ps.setString(2, activity.getActivityType());
            // target_id is VARCHAR — store as String (may be null)
            if (activity.getTargetId() != null) {
                ps.setString(3, activity.getTargetId());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            ps.setString(4, activity.getTargetType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error logging user activity: " + e.getMessage());
            return false;
        }
    }

    /**
     * Legacy helper — accepts Long targetId, converts to String for storage.
     */
    public boolean logActivity(int userId, String activityType, Long targetId, String targetType) {
        UserActivity a = new UserActivity(userId, activityType,
                targetId != null ? String.valueOf(targetId) : null, targetType);
        return logActivity(a);
    }

    // ── Analytics queries ─────────────────────────────────────────────────────

    /**
     * Returns destination IDs (as Long) most visited by a specific user.
     * target_id is stored as VARCHAR so we cast it to UNSIGNED for the query.
     */
    public List<Long> getMostVisitedDestinations(int userId, int limit) {
        List<Long> ids = new ArrayList<>();
        if (!checkConnection()) return ids;
        String sql = "SELECT target_id, COUNT(*) AS visit_count " +
                     "FROM user_activity_log " +
                     "WHERE user_id = ? AND target_type = 'DESTINATION' " +
                     "GROUP BY target_id ORDER BY visit_count DESC LIMIT ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try { ids.add(Long.parseLong(rs.getString("target_id"))); }
                catch (NumberFormatException ignored) {}
            }
        } catch (SQLException e) {
            System.err.println("Error getting most visited destinations: " + e.getMessage());
        }
        return ids;
    }

    /**
     * Returns destination IDs (as Long) most visited globally (all users).
     */
    public List<Long> getGlobalMostVisitedDestinations(int limit) {
        List<Long> ids = new ArrayList<>();
        if (!checkConnection()) return ids;
        String sql = "SELECT target_id, COUNT(*) AS visit_count " +
                     "FROM user_activity_log " +
                     "WHERE target_type = 'DESTINATION' " +
                     "GROUP BY target_id ORDER BY visit_count DESC LIMIT ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try { ids.add(Long.parseLong(rs.getString("target_id"))); }
                catch (NumberFormatException ignored) {}
            }
        } catch (SQLException e) {
            System.err.println("Error getting global most visited: " + e.getMessage());
        }
        return ids;
    }

    /**
     * Returns recommended destination IDs based on other users' clicks
     * that the given user has NOT yet visited.
     */
    public List<Long> getRecommendationsByClicks(int userId, int limit) {
        List<Long> recommendations = new ArrayList<>();
        if (!checkConnection()) return recommendations;
        String sql = "SELECT target_id, COUNT(*) AS cnt " +
                     "FROM user_activity_log " +
                     "WHERE activity_type = 'CLICK' AND target_type = 'DESTINATION' " +
                     "AND target_id NOT IN (" +
                     "  SELECT target_id FROM user_activity_log WHERE user_id = ?" +
                     ") " +
                     "GROUP BY target_id ORDER BY cnt DESC LIMIT ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try { recommendations.add(Long.parseLong(rs.getString("target_id"))); }
                catch (NumberFormatException ignored) {}
            }
        } catch (SQLException e) {
            System.err.println("Error getting recommendations: " + e.getMessage());
        }
        return recommendations;
    }
}
