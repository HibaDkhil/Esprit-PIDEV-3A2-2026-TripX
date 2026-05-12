package tn.esprit.entities;

import java.util.Date;

public class Comments {

    private int id;
    private Integer postId;         // nullable
    private Integer travelStoryId;  // nullable
    private int userId;
    private Integer parentCommentId; // nullable
    private String body;
    private Date createdAt;

    public Comments(int postId, int userId, Object o, String trim) {}

    // ✅ Constructor insertion (post OR story)
    public Comments(Integer postId, Integer travelStoryId, int userId, Integer parentCommentId, String body) {
        this.postId = postId;
        this.travelStoryId = travelStoryId;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
        this.body = body;
    }

    // ✅ Constructor complet
    public Comments(int id, Integer postId, Integer travelStoryId, int userId, Integer parentCommentId, String body, Date createdAt) {
        this.id = id;
        this.postId = postId;
        this.travelStoryId = travelStoryId;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
        this.body = body;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public Integer getTravelStoryId() { return travelStoryId; }
    public void setTravelStoryId(Integer travelStoryId) { this.travelStoryId = travelStoryId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Integer parentCommentId) { this.parentCommentId = parentCommentId; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
