package tn.esprit.entities;

public class Destination {

    private int idDestination;
    private String name;

    public Destination(int idDestination, String name) {
        this.idDestination = idDestination;
        this.name          = name;
    }

    public Destination(String name) {
        this.name = name;
    }

    public int getIdDestination()           { return idDestination; }
    public void setIdDestination(int id)    { this.idDestination = id; }

    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }

    @Override
    public String toString() {
        return "Destination{id=" + idDestination + ", name='" + name + "'}";
    }
}
