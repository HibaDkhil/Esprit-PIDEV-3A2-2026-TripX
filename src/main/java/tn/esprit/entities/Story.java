package tn.esprit.entities;

import java.util.Date;

public class Story {
    private int id;
    private int userId;
    private String imageUrl;
    private String caption;
    private Date createdAt;
    private Date expiresAt;

    public Story() {}

    public Story(int userId, String imageUrl, String caption, Date expiresAt) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.expiresAt = expiresAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
}