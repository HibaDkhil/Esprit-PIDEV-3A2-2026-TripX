package tn.esprit.tests;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class SecurityDemoTest {

    @Test
    public void testPasswordHashing() {
        String rawPassword = "mySecurePassword123!";
        
        // 1. Hash the password
        System.out.println("--- Password Hashing Demo ---");
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Hashed Password: " + hashedPassword);
        
        // 2. Verify the password
        assertTrue(BCrypt.checkpw(rawPassword, hashedPassword), "Password should match its hash");
        assertFalse(BCrypt.checkpw("wrongPassword", hashedPassword), "Incorrect password should not match");
        
        System.out.println("Verification Result: SUCCESS\n");
    }

    @Test
    public void testVerificationCodeGeneration() {
        System.out.println("--- Verification Code Demo ---");
        
        // Generate a 6-digit random code
        String code = String.format("%06d", new Random().nextInt(999999));
        
        System.out.println("Generated Code: " + code);
        assertEquals(6, code.length(), "Code should be 6 digits");
        assertTrue(code.matches("\\d{6}"), "Code should contain only digits");
        
        System.out.println("Generation Result: SUCCESS\n");
    }

    @Test
    public void testLegacyPasswordFallbackDemo() {
        System.out.println("--- Legacy Fallback Demo ---");
        String legacyPlainText = "oldPassword123";
        String userInput = "oldPassword123";
        
        boolean match;
        try {
            // This will throw IllegalArgumentException because it's not a valid BCrypt hash
            match = BCrypt.checkpw(userInput, legacyPlainText);
        } catch (IllegalArgumentException e) {
            System.out.println("BCrypt detected legacy/plain-text password. Falling back to simple equals...");
            match = userInput.equals(legacyPlainText);
        }
        
        assertTrue(match, "Legacy password should match correctly via fallback");
        System.out.println("Legacy Matching Result: SUCCESS\n");
    }
}
