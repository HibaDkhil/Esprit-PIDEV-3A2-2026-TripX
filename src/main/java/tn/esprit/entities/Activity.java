package tn.esprit.entities;

public class Activity {

    private int idActivity;
    private String name;

    public Activity(int idActivity, String name) {
        this.idActivity = idActivity;
        this.name       = name;
    }

    public Activity(String name) {
        this.name = name;
    }

    public int getIdActivity()              { return idActivity; }
    public void setIdActivity(int id)       { this.idActivity = id; }

    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }

    @Override
    public String toString() {
        return "Activity{id=" + idActivity + ", name='" + name + "'}";
    }
}
