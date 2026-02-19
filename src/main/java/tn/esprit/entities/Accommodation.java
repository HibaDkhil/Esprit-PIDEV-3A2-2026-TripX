package tn.esprit.entities;

public class Accommodation {

    private int idAccommodation;
    private String name;

    public Accommodation(int idAccommodation, String name) {
        this.idAccommodation = idAccommodation;
        this.name            = name;
    }

    public Accommodation(String name) {
        this.name = name;
    }

    public int getIdAccommodation()             { return idAccommodation; }
    public void setIdAccommodation(int id)      { this.idAccommodation = id; }

    public String getName()                     { return name; }
    public void setName(String name)            { this.name = name; }

    @Override
    public String toString() {
        return "Accommodation{id=" + idAccommodation + ", name='" + name + "'}";
    }
}
