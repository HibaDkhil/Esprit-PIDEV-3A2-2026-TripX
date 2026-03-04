package tn.esprit.services;

import tn.esprit.entities.User;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.*;

public class ReactionsService {

    public enum ReactType { LIKE, LOVE, HAHA, WOW, SAD, ANGRY, OTHER }

    private final Connection conx;

    public ReactionsService() {
        conx = MyDB.getInstance().getConx();
    }

    // =========================================================
    // POSTS
    // =========================================================
    public int countReactionsForPost(int postId) {
        String sql = "SELECT COUNT(*) FROM reactions WHERE post_id = ? AND comment_id IS NULL";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logSql("countReactionsForPost", e);
        }
        return 0;
    }

    public ReactType getUserReactionForPost(int userId, int postId) {
        String sql = """
            SELECT type
            FROM reactions
            WHERE user_id = ? AND post_id = ? AND comment_id IS NULL
            LIMIT 1
        """;
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return fromDb(rs.getString("type"));
            }
        } catch (SQLException e) {
            logSql("getUserReactionForPost", e);
        }
        return null;
    }

    public boolean toggleReactionForPost(int userId, int postId, ReactType newType) {
        ReactType existing = getUserReactionForPost(userId, postId);
        if (existing != null && existing == newType) return removeReactionForPost(userId, postId);
        removeReactionForPost(userId, postId);
        return addReactionForPost(userId, postId, newType);
    }

    public boolean addReactionForPost(int userId, int postId, ReactType type) {
        String sql = """
            INSERT INTO reactions (user_id, post_id, travel_story_id, comment_id, type, created_at)
            VALUES (?, ?, NULL, NULL, ?, NOW())
        """;
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            ps.setString(3, toDb(type));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logSql("addReactionForPost", e);
            return false;
        }
    }

    public boolean removeReactionForPost(int userId, int postId) {
        String sql = "DELETE FROM reactions WHERE user_id = ? AND post_id = ? AND comment_id IS NULL";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logSql("removeReactionForPost", e);
            return false;
        }
    }

    // =========================================================
    // TRAVEL STORIES
    // =========================================================
    public int countReactionsForStory(int storyId) {
        String sql = "SELECT COUNT(*) FROM reactions WHERE travel_story_id = ? AND comment_id IS NULL";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, storyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logSql("countReactionsForStory", e);
        }
        return 0;
    }

    public ReactType getUserReactionForStory(int userId, int storyId) {
        String sql = """
            SELECT type
            FROM reactions
            WHERE user_id = ? AND travel_story_id = ? AND comment_id IS NULL
            LIMIT 1
        """;
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, storyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return fromDb(rs.getString("type"));
            }
        } catch (SQLException e) {
            logSql("getUserReactionForStory", e);
        }
        return null;
    }

    public boolean toggleReactionForStory(int userId, int storyId, ReactType newType) {
        ReactType existing = getUserReactionForStory(userId, storyId);
        if (existing != null && existing == newType) return removeReactionForStory(userId, storyId);
        removeReactionForStory(userId, storyId);
        return addReactionForStory(userId, storyId, newType);
    }

    public boolean addReactionForStory(int userId, int storyId, ReactType type) {
        String sql = """
            INSERT INTO reactions (user_id, post_id, travel_story_id, comment_id, type, created_at)
            VALUES (?, NULL, ?, NULL, ?, NOW())
        """;
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, storyId);
            ps.setString(3, toDb(type));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logSql("addReactionForStory", e);
            return false;
        }
    }

    public boolean removeReactionForStory(int userId, int storyId) {
        String sql = "DELETE FROM reactions WHERE user_id = ? AND travel_story_id = ? AND comment_id IS NULL";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, storyId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logSql("removeReactionForStory", e);
            return false;
        }
    }

    // =========================================================
    // COMMENTS
    // =========================================================
    public ReactType getUserReactionForComment(int userId, int commentId) {
        String sql = """
            SELECT type
            FROM reactions
            WHERE user_id = ? AND comment_id = ?
            LIMIT 1
        """;
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return fromDb(rs.getString("type"));
            }
        } catch (SQLException e) {
            logSql("getUserReactionForComment", e);
        }
        return null;
    }

    public boolean toggleReactionForComment(int userId, int commentId, ReactType newType) {
        ReactType existing = getUserReactionForComment(userId, commentId);
        if (existing != null && existing == newType) return removeReactionForComment(userId, commentId);
        removeReactionForComment(userId, commentId);
        return addReactionForComment(userId, commentId, newType);
    }

    public boolean addReactionForComment(int userId, int commentId, ReactType type) {
        String sql = """
            INSERT INTO reactions (user_id, post_id, travel_story_id, comment_id, type, created_at)
            VALUES (?, NULL, NULL, ?, ?, NOW())
        """;
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, commentId);
            ps.setString(3, toDb(type));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logSql("addReactionForComment", e);
            return false;
        }
    }

    public boolean removeReactionForComment(int userId, int commentId) {
        String sql = "DELETE FROM reactions WHERE user_id = ? AND comment_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, commentId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logSql("removeReactionForComment", e);
            return false;
        }
    }

    public List<User> listReactorsForComment(int commentId, UserService userService) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id FROM reactions WHERE comment_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = userService.findById(rs.getInt("user_id"));
                    if (u != null) list.add(u);
                }
            }
        } catch (SQLException e) {
            logSql("listReactorsForComment", e);
        }
        return list;
    }

    // =========================================================
    // Helpers
    // =========================================================
    private String toDb(ReactType t) {
        if (t == null) return "other";
        return switch (t) {
            case LIKE -> "like";
            case LOVE -> "love";
            case HAHA -> "haha";
            case WOW -> "wow";
            case SAD -> "sad";
            case ANGRY -> "angry";
            case OTHER -> "other";
        };
    }

    private ReactType fromDb(String s) {
        if (s == null) return null;
        s = s.trim().toLowerCase();
        return switch (s) {
            case "like" -> ReactType.LIKE;
            case "love" -> ReactType.LOVE;
            case "haha" -> ReactType.HAHA;
            case "wow" -> ReactType.WOW;
            case "sad" -> ReactType.SAD;
            case "angry" -> ReactType.ANGRY;
            case "other" -> ReactType.OTHER;
            default -> null;
        };
    }

    private void logSql(String where, SQLException e) {
        System.err.println("❌ " + where + " SQL error:");
        System.err.println("   Message : " + e.getMessage());
        System.err.println("   SQLState: " + e.getSQLState());
        System.err.println("   Code    : " + e.getErrorCode());
        e.printStackTrace();
    }
}
