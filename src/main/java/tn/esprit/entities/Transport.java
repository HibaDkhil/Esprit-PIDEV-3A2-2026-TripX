package tn.esprit.entities;

public class Transport {

    private int idTransport;
    private String type;

    public Transport(int idTransport, String type) {
        this.idTransport = idTransport;
        this.type        = type;
    }

    public Transport(String type) {
        this.type = type;
    }

    public int getIdTransport()             { return idTransport; }
    public void setIdTransport(int id)      { this.idTransport = id; }

    public String getType()                 { return type; }
    public void setType(String type)        { this.type = type; }

    @Override
    public String toString() {
        return "Transport{id=" + idTransport + ", type='" + type + "'}";
    }
}
