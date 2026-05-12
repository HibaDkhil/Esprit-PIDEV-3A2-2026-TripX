package tn.esprit.entities;

import java.util.Date;

public class Followings {
    private int id;
    private int followerId;
    private int followedId;
    private Date createdAt;

    public Followings() {}

    public Followings(int followerId, int followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
    }

    public Followings(int id, int followerId, int followedId, Date createdAt) {
        this.id = id;
        this.followerId = followerId;
        this.followedId = followedId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFollowerId() { return followerId; }
    public void setFollowerId(int followerId) { this.followerId = followerId; }

    public int getFollowedId() { return followedId; }
    public void setFollowedId(int followedId) { this.followedId = followedId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
