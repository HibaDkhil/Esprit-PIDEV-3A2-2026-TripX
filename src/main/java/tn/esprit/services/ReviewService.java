package tn.esprit.services;

import tn.esprit.entities.Review;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewService {

    private Connection cnx;

    public ReviewService() {
        cnx = MyDB.getInstance().getConx();
    }

    public boolean addReview(Review review) {
        String sql = "INSERT INTO reviews (user_id, target_type, target_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, review.getUserId());
            ps.setString(2, review.getTargetType().name());
            ps.setLong(3, review.getTargetId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                recalculateAverageRating(review.getTargetType(), review.getTargetId());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Review> getReviews(Review.TargetType targetType, long targetId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE target_type = ? AND target_id = ? ORDER BY created_at DESC";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, targetType.name());
            ps.setLong(2, targetId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public double getAverageRating(Review.TargetType targetType, long targetId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM reviews WHERE target_type = ? AND target_id = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, targetType.name());
            ps.setLong(2, targetId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double avg = rs.getDouble("avg_rating");
                if (!rs.wasNull()) return avg;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void recalculateAverageRating(Review.TargetType targetType, long targetId) {
        double avg = getAverageRating(targetType, targetId);
        String updateSql;
        if (targetType == Review.TargetType.DESTINATION) {
            updateSql = "UPDATE destinations SET average_rating = ? WHERE destination_id = ?";
        } else {
            updateSql = "UPDATE activities SET average_rating = ? WHERE activity_id = ?";
        }
        try {
            PreparedStatement ps = cnx.prepareStatement(updateSql);
            ps.setDouble(1, avg);
            ps.setLong(2, targetId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Review> getReviewsByUser(int userId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE user_id = ? ORDER BY created_at DESC";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public boolean deleteReview(long reviewId) {
        // Get review first to recalculate rating after delete
        String selectSql = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            PreparedStatement selectPs = cnx.prepareStatement(selectSql);
            selectPs.setLong(1, reviewId);
            ResultSet rs = selectPs.executeQuery();
            if (rs.next()) {
                Review review = mapResultSetToReview(rs);
                String deleteSql = "DELETE FROM reviews WHERE review_id = ?";
                PreparedStatement deletePs = cnx.prepareStatement(deleteSql);
                deletePs.setLong(1, reviewId);
                int rows = deletePs.executeUpdate();
                if (rows > 0) {
                    recalculateAverageRating(review.getTargetType(), review.getTargetId());
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setReviewId(rs.getLong("review_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setTargetType(Review.TargetType.valueOf(rs.getString("target_type")));
        r.setTargetId(rs.getLong("target_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}
