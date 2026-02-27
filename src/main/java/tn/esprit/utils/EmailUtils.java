package tn.esprit.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {

    // IMPORTANT: For production, use environment variables or a config file
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "comptetest740@gmail.com"; // adresse
    private static final String APP_PASSWORD = "ujke ppgy eszk qast";

    public static void sendVerificationCode(String toEmail, String code) throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", SMTP_PORT);
        prop.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_FROM));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("TRIPX - Password Reset Verification Code");

        String msg = "Hello,\n\nYour verification code is: " + code + "\n\nPlease enter this code in the application to reset your password.\n\nThank you,\nThe TRIPX Team";
        message.setText(msg);

        Transport.send(message);
        System.out.println("Email sent successfully to " + toEmail);
    }
}
