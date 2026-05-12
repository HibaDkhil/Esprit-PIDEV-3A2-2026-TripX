package tn.esprit.entities;

import java.util.Date;

public class CommunityStatistics {
    private int id;
    private int userId;
    private int postsCount;
    private int commentsCount;
    private int reactionsCount;
    private int badgesCount;
    private int followersCount;
    private String otherStats;
    private Date updatedAt;

    public CommunityStatistics() {}

    public CommunityStatistics(int userId) {
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPostsCount() { return postsCount; }
    public void setPostsCount(int postsCount) { this.postsCount = postsCount; }

    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }

    public int getReactionsCount() { return reactionsCount; }
    public void setReactionsCount(int reactionsCount) { this.reactionsCount = reactionsCount; }

    public int getBadgesCount() { return badgesCount; }
    public void setBadgesCount(int badgesCount) { this.badgesCount = badgesCount; }

    public int getFollowersCount() { return followersCount; }
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }

    public String getOtherStats() { return otherStats; }
    public void setOtherStats(String otherStats) { this.otherStats = otherStats; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
