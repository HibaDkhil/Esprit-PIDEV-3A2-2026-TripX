package tn.esprit.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import tn.esprit.entities.AccommodationBooking;
import tn.esprit.entities.Booking;
import tn.esprit.entities.Bookingtrans;
import tn.esprit.entities.PacksBooking;
import tn.esprit.utils.ApiConfig;

/**
 * Service for creating Stripe Checkout Sessions and verifying payment status.
 */
public class StripePaymentService {

    public StripePaymentService() {
        String key = ApiConfig.STRIPE_SECRET_KEY;
        if (key != null && !key.isBlank()) {
            key = key.replace("\uFEFF", "").replace("\r", "").replace("\n", "").trim();
            if (key.length() >= 2 && (key.startsWith("\"") && key.endsWith("\"") || key.startsWith("'") && key.endsWith("'")))
                key = key.substring(1, key.length() - 1).trim();
            if (key.startsWith("sk_")) {
                Stripe.apiKey = key;
            } else if (!key.isEmpty()) {
                System.err.println("Stripe: STRIPE_SECRET_KEY should start with sk_test_ or sk_live_ (got '" + key.substring(0, Math.min(8, key.length())) + "...'). Use Secret key from Stripe Dashboard → Developers → API keys.");
            }
        }
        if (Stripe.apiKey == null || Stripe.apiKey.isBlank()) {
            System.err.println("Stripe: No API key. Add STRIPE_SECRET_KEY=sk_test_... to your .env file in the project root.");
        }
    }

    /**
     * Creates a Stripe Checkout Session for the given booking.
     *
     * @param booking The booking to create payment for
     * @return The Checkout Session URL to redirect the user to, or null on failure
     */
    public String createCheckoutSession(Booking booking) {
        try {
            String curr = booking.getCurrency() != null ? booking.getCurrency().toLowerCase() : "usd";
            double amountMajor = booking.getTotalAmount();
            if ("tnd".equals(curr)) {
                amountMajor = amountMajor * ApiConfig.getTndToUsdRate();
                curr = stripeCurrency();
            }
            long amountInCents = Math.round(amountMajor * 100);

            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(ApiConfig.PAYMENT_SUCCESS_URL + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(ApiConfig.PAYMENT_CANCEL_URL)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(curr)
                                .setUnitAmount(amountInCents)
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("TripX Booking - " + booking.getBookingReference())
                                        .setDescription(
                                            "Destination: " + booking.getDestinationName() +
                                            (booking.getActivityName() != null ? " | Activity: " + booking.getActivityName() : "") +
                                            " | Guests: " + booking.getNumGuests()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();

            Session session = Session.create(params);
            return session.getUrl();

        } catch (StripeException e) {
            System.err.println("Stripe error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String stripeCurrency() {
        String c = ApiConfig.STRIPE_CURRENCY;
        return (c != null && !c.isBlank()) ? c.trim().toLowerCase() : "usd";
    }

    /**
     * Generic checkout session: product name, description, amount in major units, currency (e.g. "tnd", "usd").
     * If currency is "tnd", amount is converted to USD using TND_TO_USD rate (Stripe does not support TND on all accounts).
     */
    public String createCheckoutSession(String productName, String description, double amountMajor, String currency) {
        try {
            String curr = (currency != null ? currency : "tnd").toLowerCase();
            if ("tnd".equals(curr)) {
                amountMajor = amountMajor * ApiConfig.getTndToUsdRate();
                curr = stripeCurrency();
            }
            long amountInCents = Math.round(amountMajor * 100);
            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(ApiConfig.PAYMENT_SUCCESS_URL + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(ApiConfig.PAYMENT_CANCEL_URL)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(curr)
                                .setUnitAmount(amountInCents)
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(productName != null ? productName : "TripX Booking")
                                        .setDescription(description != null ? description : "")
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();
            Session session = Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            System.err.println("Stripe error: " + e.getMessage());
            return null;
        }
    }

    public String createCheckoutSession(AccommodationBooking b) {
        String name = "TripX Accommodation #" + b.getId();
        String desc = "Check-in " + (b.getCheckIn() != null ? b.getCheckIn().toString() : "") + " | " + b.getNumberOfGuests() + " guest(s)";
        return createCheckoutSession(name, desc, b.getTotalPrice(), "tnd");
    }

    public String createCheckoutSession(Bookingtrans b) {
        String name = "TripX Transport #" + b.getBookingId();
        String desc = b.getTotalSeats() + " seat(s) | " + (b.getBookingDate() != null ? b.getBookingDate().toString() : "");
        return createCheckoutSession(name, desc, b.getTotalPrice(), "tnd");
    }

    public String createCheckoutSession(PacksBooking b) {
        String name = "TripX Pack #" + b.getPackId();
        String desc = b.getNumTravelers() + " traveler(s)";
        double amount = b.getFinalPrice() != null ? b.getFinalPrice().doubleValue() : (b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0);
        return createCheckoutSession(name, desc, amount, "tnd");
    }

    /**
     * Retrieves a Checkout Session by its ID to verify payment status.
     *
     * @param sessionId The Stripe Checkout Session ID
     * @return The Session object, or null on failure
     */
    public Session retrieveSession(String sessionId) {
        try {
            return Session.retrieve(sessionId);
        } catch (StripeException e) {
            System.err.println("Failed to retrieve session: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if a Checkout Session has been paid.
     *
     * @param sessionId The Stripe Checkout Session ID
     * @return true if the session payment status is "paid"
     */
    public boolean isSessionPaid(String sessionId) {
        Session session = retrieveSession(sessionId);
        if (session != null) {
            return "paid".equals(session.getPaymentStatus());
        }
        return false;
    }
}
