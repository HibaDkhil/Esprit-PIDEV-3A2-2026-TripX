package tn.esprit.services;

import tn.esprit.utils.MyDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SharesService {

    private final Connection conx;

    public SharesService() {
        conx = MyDB.getInstance().getConx();
    }

    // -----------------------
    // Post shares
    // -----------------------
    public boolean addShareForPost(int userId, int postId) {
        String sql = "INSERT INTO shares (user_id, post_id, travel_story_id, created_at) VALUES (?, ?, NULL, NOW())";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addShareForPost error: " + e.getMessage());
            return false;
        }
    }

    public int countSharesForPost(int postId) {
        String sql = "SELECT COUNT(*) FROM shares WHERE post_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("countSharesForPost error: " + e.getMessage());
        }
        return 0;
    }

    // -----------------------
    // Story shares
    // -----------------------
    public boolean addShareForStory(int userId, int storyId) {
        String sql = "INSERT INTO shares (user_id, post_id, travel_story_id, created_at) VALUES (?, NULL, ?, NOW())";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, storyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addShareForStory error: " + e.getMessage());
            return false;
        }
    }

    public int countSharesForStory(int storyId) {
        String sql = "SELECT COUNT(*) FROM shares WHERE travel_story_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, storyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("countSharesForStory error: " + e.getMessage());
        }
        return 0;
    }
}