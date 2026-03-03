package tn.esprit.services;

import tn.esprit.entities.Comments;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentService {

    private final Connection conx;

    public CommentService() {
        conx = MyDB.getInstance().getConx();
    }

    // =========================================================
    // CREATE (post OR travel story) + optional parent_comment_id
    // =========================================================
    public boolean create(Comments c) {
        String sql = """
            INSERT INTO comments (post_id, travel_story_id, user_id, parent_comment_id, body, created_at)
            VALUES (?, ?, ?, ?, ?, NOW())
        """;

        try (PreparedStatement ps = conx.prepareStatement(sql)) {

            if (c.getPostId() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, c.getPostId());

            if (c.getTravelStoryId() == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, c.getTravelStoryId());

            ps.setInt(3, c.getUserId());

            if (c.getParentCommentId() == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, c.getParentCommentId());

            ps.setString(5, c.getBody());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating comment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================
    // READ ALL (Admin or debug)
    // =========================================================
    public List<Comments> findAll() {
        List<Comments> list = new ArrayList<>();
        String sql = "SELECT * FROM comments ORDER BY created_at DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error findAll comments: " + e.getMessage());
        }
        return list;
    }

    // =========================================================
    // READ top-level comments for a post (no parent)
    // =========================================================
    public List<Comments> findTopLevelByPostId(int postId) {
        List<Comments> list = new ArrayList<>();
        String sql = """
            SELECT * FROM comments
            WHERE post_id = ? AND parent_comment_id IS NULL
            ORDER BY created_at ASC
        """;

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error findTopLevelByPostId: " + e.getMessage());
        }
        return list;
    }

    // =========================================================
    // READ top-level comments for a story (no parent)
    // =========================================================
    public List<Comments> findTopLevelByTravelStoryId(int storyId) {
        List<Comments> list = new ArrayList<>();
        String sql = """
            SELECT * FROM comments
            WHERE travel_story_id = ? AND parent_comment_id IS NULL
            ORDER BY created_at ASC
        """;

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, storyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error findTopLevelByTravelStoryId: " + e.getMessage());
        }
        return list;
    }

    // =========================================================
    // READ replies of a comment
    // =========================================================
    public List<Comments> findReplies(int parentCommentId) {
        List<Comments> list = new ArrayList<>();
        String sql = """
            SELECT * FROM comments
            WHERE parent_comment_id = ?
            ORDER BY created_at ASC
        """;

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, parentCommentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error findReplies: " + e.getMessage());
        }
        return list;
    }

    // =========================================================
    // READ BY POST (includes replies) - used for count
    // =========================================================
    public List<Comments> findByPostId(int postId) {
        List<Comments> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at ASC";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error findByPostId: " + e.getMessage());
        }
        return list;
    }

    // =========================================================
    // READ BY STORY (includes replies) - used for count
    // =========================================================
    public List<Comments> findByTravelStoryId(int storyId) {
        List<Comments> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE travel_story_id = ? ORDER BY created_at ASC";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, storyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error findByTravelStoryId: " + e.getMessage());
        }
        return list;
    }

    // =========================================================
    // UPDATE (Admin version) - updates by comment id
    // (Keeps compatibility with old calls: commentService.update(c))
    // =========================================================
    public boolean update(Comments c) {
        String sql = "UPDATE comments SET body = ? WHERE id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setString(1, c.getBody());
            ps.setInt(2, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating comment: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // UPDATE (User version - owner only)
    // =========================================================
    public boolean updateComment(int commentId, int userId, String newBody) {
        String sql = "UPDATE comments SET body = ? WHERE id = ? AND user_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setString(1, newBody);
            ps.setInt(2, commentId);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating comment (owner): " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // DELETE
    // =========================================================
    public boolean delete(int commentId) {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting comment: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // Mapper
    // =========================================================
    private Comments mapRow(ResultSet rs) throws SQLException {
        return new Comments(
                rs.getInt("id"),
                (Integer) rs.getObject("post_id"),
                (Integer) rs.getObject("travel_story_id"),
                rs.getInt("user_id"),
                (Integer) rs.getObject("parent_comment_id"),
                rs.getString("body"),
                rs.getTimestamp("created_at")
        );
    }
}