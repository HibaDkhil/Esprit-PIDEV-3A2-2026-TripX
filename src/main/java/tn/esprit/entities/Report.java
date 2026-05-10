package tn.esprit.entities;

import java.util.Date;

public class Report {
    private int id;
    private int reportedById;
    private Integer reportedUserId;
    private Integer reportedPostId;
    private Integer reportedCommentId;
    private String reportType;   // user | post | comment
    private String reason;
    private Date createdAt;
    private String status;       // pending | resolved | rejected

    public Report() {}

    public Report(int reportedById, Integer reportedUserId, Integer reportedPostId,
                  Integer reportedCommentId, String reportType, String reason) {
        this.reportedById = reportedById;
        this.reportedUserId = reportedUserId;
        this.reportedPostId = reportedPostId;
        this.reportedCommentId = reportedCommentId;
        this.reportType = reportType;
        this.reason = reason;
        this.status = "pending";
    }

    public Report(int id, int reportedById, Integer reportedUserId, Integer reportedPostId,
                  Integer reportedCommentId, String reportType, String reason,
                  Date createdAt, String status) {
        this.id = id;
        this.reportedById = reportedById;
        this.reportedUserId = reportedUserId;
        this.reportedPostId = reportedPostId;
        this.reportedCommentId = reportedCommentId;
        this.reportType = reportType;
        this.reason = reason;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReportedById() { return reportedById; }
    public void setReportedById(int reportedById) { this.reportedById = reportedById; }

    public Integer getReportedUserId() { return reportedUserId; }
    public void setReportedUserId(Integer reportedUserId) { this.reportedUserId = reportedUserId; }

    public Integer getReportedPostId() { return reportedPostId; }
    public void setReportedPostId(Integer reportedPostId) { this.reportedPostId = reportedPostId; }

    public Integer getReportedCommentId() { return reportedCommentId; }
    public void setReportedCommentId(Integer reportedCommentId) { this.reportedCommentId = reportedCommentId; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
