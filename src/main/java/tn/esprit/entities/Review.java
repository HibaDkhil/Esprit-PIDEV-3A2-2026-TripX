package tn.esprit.entities;

import java.sql.Timestamp;

public class Review {

    public enum TargetType {
        DESTINATION, ACTIVITY
    }

    private long reviewId;
    private int userId;
    private TargetType targetType;
    private long targetId;
    private int rating; // 1-5
    private String comment;
    private Timestamp createdAt;

    public Review() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Review(int userId, TargetType targetType, long targetId, int rating, String comment) {
        this();
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public long getReviewId() { return reviewId; }
    public void setReviewId(long reviewId) { this.reviewId = reviewId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }

    public long getTargetId() { return targetId; }
    public void setTargetId(long targetId) { this.targetId = targetId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getStarsDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rating; i++) sb.append("★");
        for (int i = rating; i < 5; i++) sb.append("☆");
        return sb.toString();
    }
}
