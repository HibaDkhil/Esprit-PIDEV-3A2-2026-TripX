package tn.esprit.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pack {

    public enum Status { ACTIVE, INACTIVE }

    private int idPack;
    private String title;
    private String description;
    private int destinationId;
    private int accommodationId;
    private int activityId;
    private int transportId;
    private int categoryId;
    private int durationDays;
    private BigDecimal basePrice;
    private Status status;
    private LocalDateTime createdAt;

    // ---- Constructor with id (reading from DB) ----
    public Pack(int idPack, String title, String description, int destinationId,
                int accommodationId, int activityId, int transportId, int categoryId,
                int durationDays, BigDecimal basePrice, Status status, LocalDateTime createdAt) {
        this.idPack          = idPack;
        this.title           = title;
        this.description     = description;
        this.destinationId   = destinationId;
        this.accommodationId = accommodationId;
        this.activityId      = activityId;
        this.transportId     = transportId;
        this.categoryId      = categoryId;
        this.durationDays    = durationDays;
        this.basePrice       = basePrice;
        this.status          = status;
        this.createdAt       = createdAt;
    }

    // ---- Constructor without id (inserting) ----
    public Pack(String title, String description, int destinationId, int accommodationId,
                int activityId, int transportId, int categoryId, int durationDays, BigDecimal basePrice) {
        this.title           = title;
        this.description     = description;
        this.destinationId   = destinationId;
        this.accommodationId = accommodationId;
        this.activityId      = activityId;
        this.transportId     = transportId;
        this.categoryId      = categoryId;
        this.durationDays    = durationDays;
        this.basePrice       = basePrice;
        this.status          = Status.ACTIVE;
    }

    // ---- Getters & Setters ----
    public int getIdPack()                          { return idPack; }
    public void setIdPack(int idPack)               { this.idPack = idPack; }

    public String getTitle()                        { return title; }
    public void setTitle(String title)              { this.title = title; }

    public String getDescription()                  { return description; }
    public void setDescription(String description)  { this.description = description; }

    public int getDestinationId()                   { return destinationId; }
    public void setDestinationId(int id)            { this.destinationId = id; }

    public int getAccommodationId()                 { return accommodationId; }
    public void setAccommodationId(int id)          { this.accommodationId = id; }

    public int getActivityId()                      { return activityId; }
    public void setActivityId(int id)               { this.activityId = id; }

    public int getTransportId()                     { return transportId; }
    public void setTransportId(int id)              { this.transportId = id; }

    public int getCategoryId()                      { return categoryId; }
    public void setCategoryId(int id)               { this.categoryId = id; }

    public int getDurationDays()                    { return durationDays; }
    public void setDurationDays(int durationDays)   { this.durationDays = durationDays; }

    public BigDecimal getBasePrice()                { return basePrice; }
    public void setBasePrice(BigDecimal basePrice)  { this.basePrice = basePrice; }

    public Status getStatus()                       { return status; }
    public void setStatus(Status status)            { this.status = status; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime t)       { this.createdAt = t; }

    @Override
    public String toString() {
        return "Pack{" +
                "idPack=" + idPack +
                ", title='" + title + '\'' +
                ", destinationId=" + destinationId +
                ", accommodationId=" + accommodationId +
                ", activityId=" + activityId +
                ", transportId=" + transportId +
                ", categoryId=" + categoryId +
                ", durationDays=" + durationDays +
                ", basePrice=" + basePrice +
                ", status=" + status +
                '}';
    }
}
