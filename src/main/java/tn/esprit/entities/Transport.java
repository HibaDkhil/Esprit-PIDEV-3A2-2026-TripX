package tn.esprit.entities;

public class Transport {
    
    private int transportId;  // Changed from id_transport
    private String type;
    // Note: Final DB has many more fields (company, capacity, etc.)

    // Constructor with ID
    public Transport(int transportId, String type) {
        this.transportId = transportId;
        this.type = type;
    }

    // Getters and Setters
        public int getTransportId() {
        return transportId;
    }

    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "transportId=" + transportId +
                ", type='" + type + '\'' +
                '}';
    }
}
