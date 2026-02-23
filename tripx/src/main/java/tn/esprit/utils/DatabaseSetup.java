package tn.esprit.utils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void main(String[] args) {
        setupDatabase();
    }

    public static void setupDatabase() {
        Connection conx = MyDB.getInstance().getConx();
        if (conx == null) {
            System.err.println("❌ Could not connect to database.");
            return;
        }

        try (Statement stmt = conx.createStatement()) {
            // 1. Add image_url column to destinations if it doesn't exist
            System.out.println("⏳ Adding image_url column...");
            try {
                stmt.execute("ALTER TABLE destinations ADD COLUMN image_url VARCHAR(500) AFTER average_rating");
                System.out.println("✅ image_url column added.");
            } catch (SQLException e) {
                if (e.getErrorCode() == 1060 || e.getMessage().contains("Duplicate")) { // Duplicate column name
                    System.out.println("ℹ️ image_url column already exists.");
                } else {
                    throw e;
                }
            }

            // 2. Add sample destinations for diverse types (Desert, Beach, Countryside)
            System.out.println("⏳ Seeding diverse destination types...");
            
            // Desert
            stmt.execute("INSERT IGNORE INTO destinations (name, type, country, city, best_season, description, timezone, average_rating, image_url) " +
                    "VALUES ('Sahara Desert', 'desert', 'Algeria', 'Tamanrasset', 'winter', 'Experience the vast golden dunes and star-filled nights.', 'GMT+1', 4.8, 'https://images.unsplash.com/photo-1509316785289-025f5b846b35?q=80&w=250&h=150&auto=format&fit=crop')");
            
            // Beach
            stmt.execute("INSERT IGNORE INTO destinations (name, type, country, city, best_season, description, timezone, average_rating, image_url) " +
                    "VALUES ('Seychelles Beach', 'beach', 'Seychelles', 'Mahe', 'spring', 'Pristine white sands and crystal clear turquoise waters.', 'GMT+4', 4.9, 'https://images.unsplash.com/photo-1589979485637-f98c301510af?q=80&w=250&h=150&auto=format&fit=crop')");
            
            // Countryside
            stmt.execute("INSERT IGNORE INTO destinations (name, type, country, city, best_season, description, timezone, average_rating, image_url) " +
                    "VALUES ('Tuscan Countryside', 'countryside', 'Italy', 'Siena', 'autumn', 'Rolling hills, vineyards, and medieval villages.', 'GMT+1', 4.7, 'https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=250&h=150&auto=format&fit=crop')");

            // 3. Update existing destinations with some sample images (Fallback)
            System.out.println("⏳ Updating existing destinations with images...");
            stmt.execute("UPDATE destinations SET image_url = 'https://picsum.photos/seed/paris/250/150' WHERE name LIKE '%Paris%' AND image_url IS NULL");
            stmt.execute("UPDATE destinations SET image_url = 'https://picsum.photos/seed/rome/250/150' WHERE name LIKE '%Rome%' AND image_url IS NULL");
            stmt.execute("UPDATE destinations SET image_url = 'https://picsum.photos/seed/travel/250/150' WHERE image_url IS NULL");
            
            // 4. Seeding sample activities for the new destinations
            System.out.println("⏳ Seeding sample activities...");
            
            // Sahara
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'Camel Trekking', 'Sunset safari through the dunes.', 80.0, 10, 'Adventure' " +
                    "FROM destinations WHERE name = 'Sahara Desert' LIMIT 1");
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'Star Gazing', 'Night sky observation in the deep desert.', 40.0, 20, 'Relax' " +
                    "FROM destinations WHERE name = 'Sahara Desert' LIMIT 1");
            
            // Seychelles
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'Scuba Diving', 'Explore the vibrant coral reefs.', 150.0, 6, 'Adventure' " +
                    "FROM destinations WHERE name = 'Seychelles Beach' LIMIT 1");
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'Island Boat Tour', 'Visit secluded coves and lagoons.', 200.0, 12, 'Relax' " +
                    "FROM destinations WHERE name = 'Seychelles Beach' LIMIT 1");
            
            // Tuscany
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'Wine Tasting Tour', 'Visit historical vineyards and taste local wines.', 120.0, 8, 'Food' " +
                    "FROM destinations WHERE name = 'Tuscan Countryside' LIMIT 1");
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'Cooking Class', 'Learn to make authentic Italian pasta.', 95.0, 10, 'Food' " +
                    "FROM destinations WHERE name = 'Tuscan Countryside' LIMIT 1");

            // Paris (if exists)
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'Eiffel Tower Gourmet Dinner', 'Fine dining with the best view of Paris.', 250.0, 4, 'Food' " +
                    "FROM destinations WHERE name LIKE '%Paris%' LIMIT 1");
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'Louvre Private Tour', 'Skip the lines and see the masterpieces.', 120.0, 6, 'Culture' " +
                    "FROM destinations WHERE name LIKE '%Paris%' LIMIT 1");

            // Rome (if exists)
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'Colosseum Underground Tour', 'Explore the gladiators gate and tunnels.', 85.0, 12, 'Culture' " +
                    "FROM destinations WHERE name LIKE '%Rome%' LIMIT 1");
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'Vatican Museums VIP', 'Early access to the Sistine Chapel.', 110.0, 8, 'Culture' " +
                    "FROM destinations WHERE name LIKE '%Rome%' LIMIT 1");

            // Generic activities for other destinations
            stmt.execute("INSERT IGNORE INTO activities (destination_id, name, description, price, capacity, category) " +
                    "SELECT destination_id, 'City Walking Tour', 'Discover historical landmarks and local secrets.', 30.0, 15, 'Culture' " +
                    "FROM destinations WHERE type = 'city'");

            System.out.println("\n🎉 Database setup completed successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Database setup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
