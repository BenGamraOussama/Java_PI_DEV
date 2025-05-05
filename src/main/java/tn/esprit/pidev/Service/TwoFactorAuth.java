package tn.esprit.pidev.Service;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.nio.ByteBuffer;
import java.time.Instant;
import org.jboss.aerogear.security.otp.Totp;

/**
 * Service class for handling Two-Factor Authentication operations.
 * This implementation is based on the TOTP (Time-based One-Time Password) algorithm
 * as specified in RFC 6238.
 */
public class TwoFactorAuth {

    private static final int SECRET_SIZE = 20; // 160 bits
    private static final String ALGORITHM = "HmacSHA1";
    private static final int CODE_DIGITS = 6;
    private static final int PERIOD = 30; // seconds

    /**
     * Generates a random secret key for use with TOTP.
     * The key is Base32 encoded and compatible with Google Authenticator.
     * 
     * @return Base32 encoded secret key
     */
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SECRET_SIZE];
        random.nextBytes(bytes);

        // Use the AeroGear OTP library's Base32 encoding
        // This ensures compatibility with the Totp class and authenticator apps
        return org.jboss.aerogear.security.otp.api.Base32.encode(bytes);
    }

    /**
     * Formats a secret key into groups for easier reading and manual entry.
     * For example, "ABCDEFGHIJKLMNOP" becomes "ABCD EFGH IJKL MNOP".
     * 
     * @param secretKey The secret key to format
     * @param groupSize The size of each group
     * @return The formatted secret key
     */
    public static String formatSecretKey(String secretKey, int groupSize) {
        if (secretKey == null || secretKey.isEmpty()) {
            return "";
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < secretKey.length(); i++) {
            if (i > 0 && i % groupSize == 0) {
                formatted.append(" ");
            }
            formatted.append(secretKey.charAt(i));
        }

        return formatted.toString();
    }

    /**
     * Generates a URL for creating a QR code that can be scanned by
     * Google Authenticator or similar apps.
     * 
     * @param issuer The name of the service
     * @param accountName The user's account name or email
     * @param secretKey The secret key (Base32 encoded)
     * @return URL for QR code generation
     */
    public static String getQRCodeURL(String issuer, String accountName, String secretKey) {
        // Microsoft Authenticator may have issues with certain characters in the issuer or account name
        // Use simple URL encoding to ensure compatibility
        String encodedIssuer = java.net.URLEncoder.encode(issuer, java.nio.charset.StandardCharsets.UTF_8);
        String encodedAccount = java.net.URLEncoder.encode(accountName, java.nio.charset.StandardCharsets.UTF_8);

        // The secret key should not be URL-encoded as it's already Base32 encoded
        // and authenticator apps expect the raw Base32 string

        // Format: otpauth://totp/[issuer]:[account]?secret=[secret]&issuer=[issuer]&algorithm=SHA1&digits=[digits]&period=[period]
        // This format is compatible with most authenticator apps including Microsoft Authenticator
        String url = "otpauth://totp/" + encodedIssuer + ":" + encodedAccount + 
               "?secret=" + secretKey + "&issuer=" + encodedIssuer + 
               "&algorithm=SHA1&digits=" + CODE_DIGITS + "&period=" + PERIOD;

        System.out.println("Generated TOTP URL: " + url);
        return url;
    }

    /**
     * Validates a TOTP code against the secret key.
     * Uses the AeroGear OTP library for validation.
     * Implements a time window to allow for clock skew between server and client.
     * 
     * @param secretKey The secret key
     * @param code The code to validate
     * @return true if the code is valid, false otherwise
     */
    public static boolean validateCode(String secretKey, String code) {
        System.out.println("Validating code: " + code);

        if (code == null || code.length() != CODE_DIGITS) {
            System.out.println("Invalid code format: " + (code == null ? "null" : "length=" + code.length()));
            return false;
        }

        try {
            // Create a new TOTP instance with the secret key
            Totp totp = new Totp(secretKey);

            // Get current time for debugging
            long currentTimeMillis = System.currentTimeMillis();
            System.out.println("Current time: " + currentTimeMillis + " ms");
            System.out.println("Current time step: " + (currentTimeMillis / 1000 / PERIOD));

            // Try with current time using the AeroGear OTP library
            boolean isValid = totp.verify(code);
            System.out.println("Code validé avec l'heure actuelle : " + isValid);

            // If not valid, try with our custom implementation for previous and next time steps
            if (!isValid) {
                // Try with current time using our custom implementation
                String currentCode = generateTOTP(secretKey, currentTimeMillis);
                // Compare as strings to preserve leading zeros
                isValid = code.equals(currentCode);

                // If still not valid, try with -30 seconds (previous time step)
                if (!isValid) {
                    long previousTimeMillis = currentTimeMillis - (PERIOD * 1000);
                    String previousCode = generateTOTP(secretKey, previousTimeMillis);
                    // Compare as strings to preserve leading zeros
                    isValid = code.equals(previousCode);
                    System.out.println("Checking against previous time step: " + isValid);
                }

                // If still not valid, try with +30 seconds (next time step)
                if (!isValid) {
                    long nextTimeMillis = currentTimeMillis + (PERIOD * 1000);
                    String nextCode = generateTOTP(secretKey, nextTimeMillis);
                    // Compare as strings to preserve leading zeros
                    isValid = code.equals(nextCode);
                    System.out.println("Checking against next time step: " + isValid);
                }
            }

            System.out.println("Final code validation result: " + isValid);
            return isValid;
        } catch (Exception e) {
            System.out.println("Error validating TOTP code: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generates a TOTP code for a specific time.
     * This is a helper method for validateCode to check codes at different time steps.
     * 
     * @param secretKey The secret key
     * @param timeMillis The time in milliseconds
     * @return The generated TOTP code
     * @throws Exception If there's an error generating the code
     */
    private static String generateTOTP(String secretKey, long timeMillis) throws Exception {
        byte[] key = org.jboss.aerogear.security.otp.api.Base32.decode(secretKey);

        // Calculate time step from time in milliseconds
        long timeStep = timeMillis / 1000 / PERIOD;

        // Convert time step to byte array
        byte[] timeBytes = ByteBuffer.allocate(8).putLong(timeStep).array();

        // Calculate HMAC-SHA1
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(key, ALGORITHM));
        byte[] hash = mac.doFinal(timeBytes);

        // Get offset
        int offset = hash[hash.length - 1] & 0xf;

        // Calculate binary code
        int binary = ((hash[offset] & 0x7f) << 24) |
                     ((hash[offset + 1] & 0xff) << 16) |
                     ((hash[offset + 2] & 0xff) << 8) |
                     (hash[offset + 3] & 0xff);

        // Calculate decimal code and ensure it has 6 digits
        int decimal = binary % (int) Math.pow(10, CODE_DIGITS);
        String code = String.format("%0" + CODE_DIGITS + "d", decimal);

        return code;
    }

}
