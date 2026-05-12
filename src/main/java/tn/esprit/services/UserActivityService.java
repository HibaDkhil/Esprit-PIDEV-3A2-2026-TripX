package tn.esprit.services;

import tn.esprit.entities.UserActivity;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserActivityService {
    private Connection conx;

    public UserActivityService() {
        conx = MyDB.getInstance().getConx();
        if (conx == null) {
            System.err.println("❌ UserActivityService: Connection is null!");
        }
    }

    public boolean logActivity(UserActivity activity) {
        if (conx == null) return false;
        String sql = "INSERT INTO user_activity_log (user_id, activity_type, target_id, target_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, activity.getUserId());
            ps.setString(2, activity.getActivityType());
            if (activity.getTargetId() != null) {
                ps.setLong(3, activity.getTargetId());
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }
            ps.setString(4, activity.getTargetType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error logging user activity: " + e.getMessage());
            return false;
        }
    }

    public List<Long> getMostVisitedDestinations(int userId, int limit) {
        List<Long> ids = new ArrayList<>();
        if (conx == null) return ids;
        String sql = "SELECT target_id, COUNT(*) as visit_count FROM user_activity_log " +
                     "WHERE user_id = ? AND target_type = 'DESTINATION' " +
                     "GROUP BY target_id ORDER BY visit_count DESC LIMIT ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getLong("target_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting most visited destinations: " + e.getMessage());
        }
        return ids;
    }

    public List<Long> getGlobalMostVisitedDestinations(int limit) {
        List<Long> ids = new ArrayList<>();
        if (conx == null) return ids;
        String sql = "SELECT target_id, COUNT(*) as visit_count FROM user_activity_log " +
                     "WHERE target_type = 'DESTINATION' " +
                     "GROUP BY target_id ORDER BY visit_count DESC LIMIT ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getLong("target_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting global most visited: " + e.getMessage());
        }
        return ids;
    }

    public List<Long> getRecommendationsByClicks(int userId, int limit) {
        List<Long> recommendations = new ArrayList<>();
        if (conx == null) return recommendations;
        String sql = "SELECT target_id, COUNT(*) as count FROM user_activity_log " +
                     "WHERE activity_type = 'CLICK' AND target_type = 'DESTINATION' " +
                     "AND target_id NOT IN (SELECT target_id FROM user_activity_log WHERE user_id = ?) " +
                     "GROUP BY target_id ORDER BY count DESC LIMIT ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recommendations.add(rs.getLong("target_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recommendations: " + e.getMessage());
        }
        return recommendations;
    }
}
