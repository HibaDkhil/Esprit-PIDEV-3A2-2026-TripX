package tn.esprit.entities;

import java.util.Date;

public class Posts {

    private int id;
    private int userId;
    private int tripId;
    private String title;
    private String body;
    private String type;
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;
    private boolean isConfirmed;

    // ✅ Constructor vide
    public Posts() {
    }

    // ✅ Constructor sans ID (pour insertion)
    public Posts(int userId, int tripId, String title, String body, String type) {
        this.userId = userId;
        this.tripId = tripId;
        this.title = title;
        this.body = body;
        this.type = type;
        this.isConfirmed = false; // par défaut
    }

    // ✅ Constructor sans ID + imageUrl (utile si tu veux insérer avec image)
    public Posts(int userId, int tripId, String title, String body, String type, String imageUrl) {
        this.userId = userId;
        this.tripId = tripId;
        this.title = title;
        this.body = body;
        this.type = type;
        this.imageUrl = imageUrl;
        this.isConfirmed = false; // par défaut
    }

    // ✅ Constructor complet
    public Posts(int id, int userId, int tripId, String title, String body,
                 String type, String imageUrl, Date createdAt,
                 Date updatedAt, boolean isConfirmed) {
        this.id = id;
        this.userId = userId;
        this.tripId = tripId;
        this.title = title;
        this.body = body;
        this.type = type;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isConfirmed = isConfirmed;
    }

    // ✅ Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean confirmed) { isConfirmed = confirmed; }

    @Override
    public String toString() {
        return "Posts{" +
                "id=" + id +
                ", userId=" + userId +
                ", tripId=" + tripId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isConfirmed=" + isConfirmed +
                '}';
    }
}