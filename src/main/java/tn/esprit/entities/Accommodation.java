package tn.esprit.entities;

public class Accommodation {
    
    private int id;  // Changed from id_accommodation
    private String name;
    // Note: Final DB has many more fields (type, city, stars, etc.)
    // We only use what's needed for packs

    // Constructor with ID
    public Accommodation(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
        public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
