package tn.esprit.services;

import tn.esprit.entities.Posts;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostService {

    private final Connection conx;

    public PostService() {
        conx = MyDB.getInstance().getConx();
    }

    public boolean create(Posts post) {
        String sql = "INSERT INTO posts (user_id, trip_id, title, body, type, image_url, is_confirmed) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {

            ps.setInt(1, post.getUserId());

            if (post.getTripId() <= 0) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, post.getTripId());

            ps.setString(3, post.getTitle());
            ps.setString(4, post.getBody());
            ps.setString(5, post.getType());

            if (post.getImageUrl() == null || post.getImageUrl().trim().isEmpty()) ps.setNull(6, Types.VARCHAR);
            else ps.setString(6, post.getImageUrl().trim());

            ps.setBoolean(7, post.isConfirmed());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating post: " + e.getMessage());
            return false;
        }
    }

    public List<Posts> findAll() {
        List<Posts> list = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY created_at DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error finding posts: " + e.getMessage());
        }
        return list;
    }

    public List<Posts> findByUserId(int userId) {
        List<Posts> list = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding posts by user: " + e.getMessage());
        }
        return list;
    }

    public List<Posts> searchByBodyOrTitle(String keyword) {
        List<Posts> list = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE title LIKE ? OR body LIKE ? ORDER BY created_at DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching posts: " + e.getMessage());
        }
        return list;
    }

    public boolean update(Posts post) {
        String sql = "UPDATE posts SET trip_id = ?, title = ?, body = ?, type = ?, image_url = ?, " +
                "is_confirmed = ?, updated_at = NOW() WHERE id = ?";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {

            if (post.getTripId() <= 0) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, post.getTripId());

            ps.setString(2, post.getTitle());
            ps.setString(3, post.getBody());
            ps.setString(4, post.getType());

            if (post.getImageUrl() == null || post.getImageUrl().trim().isEmpty()) ps.setNull(5, Types.VARCHAR);
            else ps.setString(5, post.getImageUrl().trim());

            ps.setBoolean(6, post.isConfirmed());
            ps.setInt(7, post.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating post: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting post: " + e.getMessage());
            return false;
        }
    }

    private Posts mapRow(ResultSet rs) throws SQLException {
        return new Posts(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("trip_id"),
                rs.getString("title"),
                rs.getString("body"),
                rs.getString("type"),
                rs.getString("image_url"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at"),
                rs.getBoolean("is_confirmed")
        );
    }
}
