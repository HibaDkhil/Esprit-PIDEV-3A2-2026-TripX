package tn.esprit.services;

import tn.esprit.entities.Story;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StoryService {

    private final Connection conx;

    public StoryService() {
        conx = MyDB.getInstance().getConx();
    }

    public boolean create(Story s) {
        String sql = "INSERT INTO stories (user_id, image_url, caption, expires_at) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, s.getUserId());

            if (s.getImageUrl() == null || s.getImageUrl().trim().isEmpty()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, s.getImageUrl().trim());
            }

            if (s.getCaption() == null || s.getCaption().trim().isEmpty()) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, s.getCaption().trim());
            }

            if (s.getExpiresAt() == null) {
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis() + 24L * 60 * 60 * 1000));
            } else {
                ps.setTimestamp(4, new Timestamp(s.getExpiresAt().getTime()));
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating story: " + e.getMessage());
            return false;
        }
    }

    /** @return number of deleted rows */
    public int cleanupExpiredStories() {
        String sql = "DELETE FROM stories WHERE expires_at <= NOW()";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error cleaning expired stories: " + e.getMessage());
            return 0;
        }
    }

    /** Active stories only (auto cleans expired first) */
    public List<Story> findActiveStories() {
        cleanupExpiredStories();

        List<Story> list = new ArrayList<>();
        String sql = "SELECT * FROM stories WHERE expires_at > NOW() ORDER BY created_at DESC";

        try (PreparedStatement ps = conx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("Error finding active stories: " + e.getMessage());
        }

        return list;
    }

    public List<Story> findActiveStoriesByUserId(int userId) {
        cleanupExpiredStories();

        List<Story> list = new ArrayList<>();
        String sql = "SELECT * FROM stories WHERE user_id = ? AND expires_at > NOW() ORDER BY created_at DESC";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding stories by user: " + e.getMessage());
        }

        return list;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM stories WHERE id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting story: " + e.getMessage());
            return false;
        }
    }

    private Story mapRow(ResultSet rs) throws SQLException {
        Story s = new Story();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        s.setImageUrl(rs.getString("image_url"));
        s.setCaption(rs.getString("caption"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setExpiresAt(rs.getTimestamp("expires_at"));
        return s;
    }
}