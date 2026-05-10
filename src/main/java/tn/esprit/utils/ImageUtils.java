package tn.esprit.utils;

import javafx.scene.image.Image;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ImageUtils {

    /**
     * Returns an Image for a Destination, falling back to an internet source if no local image is found.
     */
    public static Image getDestinationImage(String imageUrl, String name) {
        // 1. Direct HTTP URL
        if (imageUrl != null && (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
            try { return new Image(imageUrl, true); } catch (Exception ignored) {}
        }
        
        // 2. Local filename from DB
        if (imageUrl != null && !imageUrl.isEmpty()) {
            URL res = ImageUtils.class.getResource("/images/destinations/" + imageUrl);
            if (res != null) {
                try { return new Image(res.toExternalForm(), true); } catch (Exception ignored) {}
            }
        }
        
        // 3. Try to guess local filename based on name
        if (name != null) {
            String[] exts = {".jpg", ".jfif", ".png", ".jpeg"};
            for (String ext : exts) {
                URL res = ImageUtils.class.getResource("/images/destinations/" + name + ext);
                if (res != null) {
                    try { return new Image(res.toExternalForm(), true); } catch (Exception ignored) {}
                }
            }
        }
        
        // 4. Internet fallback
        String query = (name != null && !name.isEmpty()) ? URLEncoder.encode(name, StandardCharsets.UTF_8) : "travel";
        String fallbackUrl = "https://loremflickr.com/400/300/travel," + query;
        return new Image(fallbackUrl, true);
    }

    /**
     * Returns an Image for an Activity, falling back to an internet source if no local image is found.
     */
    public static Image getActivityImage(String imageUrl, String name) {
        // 1. Direct HTTP URL
        if (imageUrl != null && (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
            try { return new Image(imageUrl, true); } catch (Exception ignored) {}
        }
        
        // 2. Local filename from DB
        if (imageUrl != null && !imageUrl.isEmpty()) {
            URL res = ImageUtils.class.getResource("/images/activities/" + imageUrl);
            if (res != null) {
                try { return new Image(res.toExternalForm(), true); } catch (Exception ignored) {}
            }
        }
        
        // 3. Try to guess local filename based on name
        if (name != null) {
            String[] exts = {".jpg", ".jfif", ".png", ".jpeg"};
            for (String ext : exts) {
                URL res = ImageUtils.class.getResource("/images/activities/" + name + ext);
                if (res != null) {
                    try { return new Image(res.toExternalForm(), true); } catch (Exception ignored) {}
                }
            }
        }
        
        // 4. Internet fallback
        String query = (name != null && !name.isEmpty()) ? URLEncoder.encode(name, StandardCharsets.UTF_8) : "activity";
        String fallbackUrl = "https://loremflickr.com/400/300/tourism," + query;
        return new Image(fallbackUrl, true);
    }
}
