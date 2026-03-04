package tn.esprit.services;

import tn.esprit.entities.SavedPost;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavedPostsService {

    private final Connection conx;

    public SavedPostsService() {
        conx = MyDB.getInstance().getConx();
    }

    public boolean save(int userId, int postId) {
        if (isSaved(userId, postId)) return false;
        String sql = "INSERT INTO saved_posts (user_id, post_id, saved_at) VALUES (?, ?, NOW())";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("save post error: " + e.getMessage());
            return false;
        }
    }

    public boolean unsave(int userId, int postId) {
        String sql = "DELETE FROM saved_posts WHERE user_id = ? AND post_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("unsave post error: " + e.getMessage());
            return false;
        }
    }

    public boolean toggleSave(int userId, int postId) {
        if (isSaved(userId, postId)) return unsave(userId, postId);
        return save(userId, postId);
    }

    public boolean isSaved(int userId, int postId) {
        String sql = "SELECT COUNT(*) FROM saved_posts WHERE user_id = ? AND post_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("isSaved error: " + e.getMessage());
            return false;
        }
    }

    public List<Integer> getSavedPostIds(int userId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT post_id FROM saved_posts WHERE user_id = ? ORDER BY saved_at DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("post_id"));
            }
        } catch (SQLException e) {
            System.err.println("getSavedPostIds error: " + e.getMessage());
        }
        return ids;
    }

    public List<SavedPost> findByUserId(int userId) {
        List<SavedPost> list = new ArrayList<>();
        String sql = "SELECT * FROM saved_posts WHERE user_id = ? ORDER BY saved_at DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("findByUserId saved_posts error: " + e.getMessage());
        }
        return list;
    }

    public int countSavesForPost(int postId) {
        String sql = "SELECT COUNT(*) FROM saved_posts WHERE post_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("countSavesForPost error: " + e.getMessage());
            return 0;
        }
    }

    private SavedPost mapRow(ResultSet rs) throws SQLException {
        return new SavedPost(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("post_id"),
                rs.getTimestamp("saved_at")
        );
    }
}
