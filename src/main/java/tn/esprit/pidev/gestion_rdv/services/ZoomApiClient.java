package tn.esprit.pidev.gestion_rdv.services;

import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Client for interacting with the Zoom API.
 * This is a simplified implementation that simulates API calls.
 */
public class ZoomApiClient {
    private final String apiKey;
    private final String apiSecret;

    /**
     * Creates a new ZoomApiClient with the specified API key and secret.
     * @param apiKey The Zoom API key
     * @param apiSecret The Zoom API secret
     */
    public ZoomApiClient(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    /**
     * Creates a meeting using the Zoom API.
     * This is a simplified implementation that simulates the API call.
     * @param meeting The meeting to create
     * @return The created meeting with a join URL
     * @throws Exception If an error occurs during the API call
     */
    public Meeting createMeeting(Meeting meeting) throws Exception {
        // In a real implementation, this would make an HTTP request to the Zoom API
        // For now, we'll simulate the API call and return a meeting with a join URL
        
        // Validate required fields
        if (meeting.getTopic() == null || meeting.getTopic().isEmpty()) {
            throw new IllegalArgumentException("Meeting topic is required");
        }
        if (meeting.getStartTime() == null || meeting.getStartTime().isEmpty()) {
            throw new IllegalArgumentException("Meeting start time is required");
        }
        
        // Generate a JWT token for authentication (simplified)
        String token = generateJWT();
        
        // Simulate API response
        String meetingId = String.valueOf(Math.abs(meeting.getTopic().hashCode()));
        String joinUrl = "https://zoom.us/j/" + meetingId + "?pwd=" + meeting.getPassword();
        
        // Set the join URL on the meeting
        meeting.setJoinUrl(joinUrl);
        
        return meeting;
    }
    
    /**
     * Generates a JWT token for authentication with the Zoom API.
     * This is a simplified implementation.
     * @return The JWT token
     */
    private String generateJWT() {
        try {
            long expiration = Instant.now().plusSeconds(60 * 60).getEpochSecond(); // 1 hour
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payload = "{\"iss\":\"" + apiKey + "\",\"exp\":" + expiration + "}";
            
            String headerEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
            String payloadEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
            
            String toSign = headerEncoded + "." + payloadEncoded;
            
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
            hmac.init(keySpec);
            
            byte[] signatureBytes = hmac.doFinal(toSign.getBytes());
            String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
            
            return headerEncoded + "." + payloadEncoded + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT", e);
        }
    }
}