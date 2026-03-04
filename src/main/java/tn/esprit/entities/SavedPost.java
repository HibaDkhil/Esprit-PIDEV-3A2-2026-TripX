package tn.esprit.entities;

import java.util.Date;

public class SavedPost {
    private int id;
    private int userId;
    private int postId;
    private Date savedAt;

    public SavedPost() {}

    public SavedPost(int userId, int postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public SavedPost(int id, int userId, int postId, Date savedAt) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.savedAt = savedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public Date getSavedAt() { return savedAt; }
    public void setSavedAt(Date savedAt) { this.savedAt = savedAt; }
}
