package tn.esprit.entities;

public class Destination {
    
    private long destinationId;  // Changed from int id_destination
    private String name;
    // Note: Final DB has many more fields (country, continent, lat, long, etc.)
    // We only use the fields needed for packs module

    // Constructor with ID (for reading from DB)
    public Destination(long destinationId, String name) {
        this.destinationId = destinationId;
        this.name = name;
    }

    // Getters and Setters
    public long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(long destinationId) {
        this.destinationId = destinationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "destinationId=" + destinationId +
                ", name='" + name + '\'' +
                '}';
    }
}
