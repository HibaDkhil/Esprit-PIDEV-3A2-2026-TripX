package tn.esprit.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Offer {

    public enum DiscountType { PERCENTAGE, FIXED }

    private int idOffer;
    private String title;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private Integer packId;          // nullable
    private Integer destinationId;   // nullable
    private Integer accommodationId; // nullable
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;

    // ---- Constructor with id (reading from DB) ----
    public Offer(int idOffer, String title, String description, DiscountType discountType,
                 BigDecimal discountValue, Integer packId, Integer destinationId,
                 Integer accommodationId, LocalDate startDate, LocalDate endDate, boolean isActive) {
        this.idOffer         = idOffer;
        this.title           = title;
        this.description     = description;
        this.discountType    = discountType;
        this.discountValue   = discountValue;
        this.packId          = packId;
        this.destinationId   = destinationId;
        this.accommodationId = accommodationId;
        this.startDate       = startDate;
        this.endDate         = endDate;
        this.isActive        = isActive;
    }

    // ---- Constructor without id (inserting) ----
    public Offer(String title, String description, DiscountType discountType,
                 BigDecimal discountValue, Integer packId, Integer destinationId,
                 Integer accommodationId, LocalDate startDate, LocalDate endDate) {
        this.title           = title;
        this.description     = description;
        this.discountType    = discountType;
        this.discountValue   = discountValue;
        this.packId          = packId;
        this.destinationId   = destinationId;
        this.accommodationId = accommodationId;
        this.startDate       = startDate;
        this.endDate         = endDate;
        this.isActive        = true;
    }

    // ---- Getters & Setters ----
    public int getIdOffer()                         { return idOffer; }
    public void setIdOffer(int idOffer)             { this.idOffer = idOffer; }

    public String getTitle()                        { return title; }
    public void setTitle(String title)              { this.title = title; }

    public String getDescription()                  { return description; }
    public void setDescription(String description)  { this.description = description; }

    public DiscountType getDiscountType()           { return discountType; }
    public void setDiscountType(DiscountType t)     { this.discountType = t; }

    public BigDecimal getDiscountValue()            { return discountValue; }
    public void setDiscountValue(BigDecimal v)      { this.discountValue = v; }

    public Integer getPackId()                      { return packId; }
    public void setPackId(Integer packId)           { this.packId = packId; }

    public Integer getDestinationId()               { return destinationId; }
    public void setDestinationId(Integer id)        { this.destinationId = id; }

    public Integer getAccommodationId()             { return accommodationId; }
    public void setAccommodationId(Integer id)      { this.accommodationId = id; }

    public LocalDate getStartDate()                 { return startDate; }
    public void setStartDate(LocalDate startDate)   { this.startDate = startDate; }

    public LocalDate getEndDate()                   { return endDate; }
    public void setEndDate(LocalDate endDate)       { this.endDate = endDate; }

    public boolean isActive()                       { return isActive; }
    public void setActive(boolean active)           { this.isActive = active; }

    @Override
    public String toString() {
        return "Offer{" +
                "idOffer=" + idOffer +
                ", title='" + title + '\'' +
                ", discountType=" + discountType +
                ", discountValue=" + discountValue +
                ", packId=" + packId +
                ", destinationId=" + destinationId +
                ", accommodationId=" + accommodationId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isActive=" + isActive +
                '}';
    }
}
