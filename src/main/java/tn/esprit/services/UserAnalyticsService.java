package tn.esprit.services;

import tn.esprit.entities.UserActivity;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class UserAnalyticsService {
    private Connection conx;

    public UserAnalyticsService() {
        conx = MyDB.getInstance().getConx();
    }

    /**
     * Get the most used features for a user.
     * Features are stored in target_type as 'PAGE:name' or 'FEATURE:name'.
     */
    public Map<String, Integer> getMostUsedFeatures(int userId) {
        Map<String, Integer> features = new LinkedHashMap<>();
        if (conx == null) return features;

        String sql = "SELECT target_type, COUNT(*) as count FROM user_activity_log " +
                "WHERE user_id = ? AND (target_type LIKE 'PAGE:%' OR target_type LIKE 'FEATURE:%') " +
                "GROUP BY target_type ORDER BY count DESC LIMIT 5";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String raw = rs.getString("target_type");
                String featureName = raw.contains(":") ? raw.split(":")[1] : raw;
                
                // Human-friendly labels
                featureName = switch (featureName) {
                    case "HOME" -> "Home Dashboard";
                    case "PROFILE" -> "User Profile";
                    case "AI_CHAT" -> "AI Assistant";
                    case "CLIMATE_MATCH" -> "Climate Matcher";
                    case "TRIP_PLANNER" -> "AI Trip Planner";
                    default -> featureName;
                };
                
                features.put(featureName, rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting most used features: " + e.getMessage());
        }
        return features;
    }

    /**
     * Get the search history for a user.
     */
    public List<String> getSearchHistory(int userId) {
        List<String> history = new ArrayList<>();
        if (conx == null) return history;

        String sql = "SELECT target_type FROM user_activity_log " +
                "WHERE user_id = ? AND activity_type = 'SEARCH' " +
                "ORDER BY timestamp DESC LIMIT 10";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String raw = rs.getString("target_type");
                if (raw != null && raw.startsWith("QUERY:")) {
                    history.add(raw.substring(6));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting search history: " + e.getMessage());
        }
        return history;
    }

    /**
     * Get activity counts by hour of the day.
     */
    public Map<Integer, Integer> getActiveHours(int userId) {
        Map<Integer, Integer> hours = new TreeMap<>();
        if (conx == null) return hours;

        String sql = "SELECT HOUR(timestamp) as hr, COUNT(*) as count FROM user_activity_log " +
                "WHERE user_id = ? " +
                "GROUP BY HOUR(timestamp) ORDER BY hr";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                hours.put(rs.getInt("hr"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active hours: " + e.getMessage());
        }
        return hours;
    }

    /**
     * Get recommendations based on activity and user preferences.
     */
    public List<Long> getRecommendations(int userId, int limit) {
        List<Long> recommendedIds = new ArrayList<>();
        if (conx == null) return recommendedIds;

        String sql = "SELECT target_id, COUNT(*) as weight FROM user_activity_log " +
                "WHERE target_type = 'DESTINATION' " +
                "AND target_id NOT IN (SELECT target_id FROM user_activity_log WHERE user_id = ? AND target_type = 'DESTINATION') " +
                "GROUP BY target_id ORDER BY weight DESC LIMIT ?";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recommendedIds.add(rs.getLong("target_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recommendations: " + e.getMessage());
        }
        return recommendedIds;
    }

    /**
     * Consistently log user activity.
     */
    public void logActivity(int userId, String activityType, String targetType, Long targetId) {
        if (conx == null) return;

        String sql = "INSERT INTO user_activity_log (user_id, activity_type, target_id, target_type) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, activityType);
            if (targetId != null) {
                ps.setLong(3, targetId);
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }
            ps.setString(4, targetType);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging activity: " + e.getMessage());
        }
    }
}