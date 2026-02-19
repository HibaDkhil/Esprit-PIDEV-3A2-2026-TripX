package tn.esprit;

import tn.esprit.services.*;
import tn.esprit.entities.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        // -----------------------------------------------
        // TEST LookupService (prefilled tables)
        // -----------------------------------------------
        LookupService lookupService = new LookupService();
        try {
            System.out.println("\n=== Destinations ===");
            lookupService.getAllDestinations().forEach(System.out::println);

            System.out.println("\n=== Activities ===");
            lookupService.getAllActivities().forEach(System.out::println);

            System.out.println("\n=== Accommodations ===");
            lookupService.getAllAccommodations().forEach(System.out::println);

            System.out.println("\n=== Transport ===");
            lookupService.getAllTransport().forEach(System.out::println);

        } catch (SQLException e) {
            System.out.println("LookupService error: " + e.getMessage());
        }

        // -----------------------------------------------
        // TEST PackCategoryService
        // -----------------------------------------------
        PackCategoryService categoryService = new PackCategoryService();
        try {
            System.out.println("\n=== Categories ===");
            categoryService.afficherList().forEach(System.out::println);

        } catch (SQLException e) {
            System.out.println("CategoryService error: " + e.getMessage());
        }

        // -----------------------------------------------
        // TEST PackService - Add then Read
        // -----------------------------------------------
        PackService packService = new PackService();
        try {
            // Add a test pack (destination=1:Spain, accommodation=1, activity=1, transport=1, category=1)
            Pack testPack = new Pack(
                    "Spain Adventure",
                    "A fun trip to Spain",
                    1, 1, 1, 1, 1,
                    7,
                    new BigDecimal("599.99")
            );
            packService.add(testPack);

            System.out.println("\n=== All Packs ===");
            packService.afficherList().forEach(System.out::println);

            System.out.println("\n=== Active Packs ===");
            packService.getActivePacks().forEach(System.out::println);

        } catch (SQLException e) {
            System.out.println("PackService error: " + e.getMessage());
        }

        // -----------------------------------------------
        // TEST OfferService - Add then Read
        // -----------------------------------------------
        OfferService offerService = new OfferService();
        try {
            // Add a test offer on pack_id=1, 10% off
            Offer testOffer = new Offer(
                    "Summer Deal",
                    "10% off on Spain Adventure",
                    Offer.DiscountType.PERCENTAGE,
                    new BigDecimal("10.00"),
                    1,    // pack_id
                    null, // destination_id (not used here)
                    null, // accommodation_id (not used here)
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 12, 31)
            );
            offerService.add(testOffer);

            System.out.println("\n=== All Offers ===");
            offerService.afficherList().forEach(System.out::println);

            System.out.println("\n=== Active Offers Today ===");
            offerService.getActiveOffers().forEach(System.out::println);

        } catch (SQLException e) {
            System.out.println("OfferService error: " + e.getMessage());
        }

        // -----------------------------------------------
        // TEST LoyaltyPointsService
        // -----------------------------------------------
        LoyaltyPointsService loyaltyService = new LoyaltyPointsService();
        try {
            // Simulate user id=1 completing a trip
            System.out.println("\n=== Simulate trip for user id=1 ===");
            loyaltyService.addTripPoints(1);

            System.out.println("\n=== Loyalty Points ===");
            loyaltyService.afficherList().forEach(System.out::println);

            // Final price: pack costs 599.99, offer = 10%, user loyalty = 4% (Bronze) → 14% total off
            double finalPrice = loyaltyService.calculateFinalPrice(599.99, 1, 10.0);
            System.out.println("\nFinal price for 599.99 pack with 10% offer + Bronze loyalty (4%) = "
                    + String.format("%.2f", finalPrice) + " TND");

        } catch (SQLException e) {
            System.out.println("LoyaltyService error: " + e.getMessage());
        }
    }
}
