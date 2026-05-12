package tn.esprit.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import tn.esprit.entities.Booking;
import tn.esprit.utils.ApiConfig;

/**
 * Service for creating Stripe Checkout Sessions and verifying payment status.
 */
public class StripePaymentService {

    public StripePaymentService() {
        Stripe.apiKey = ApiConfig.STRIPE_SECRET_KEY;
    }

    /**
     * Creates a Stripe Checkout Session for the given booking.
     *
     * @param booking The booking to create payment for
     * @return The Checkout Session URL to redirect the user to, or null on failure
     */
    public String createCheckoutSession(Booking booking) {
        try {
            // Stripe expects amount in cents (smallest currency unit)
            long amountInCents = Math.round(booking.getTotalAmount() * 100);

            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(ApiConfig.PAYMENT_SUCCESS_URL + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(ApiConfig.PAYMENT_CANCEL_URL)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(booking.getCurrency().toLowerCase())
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
