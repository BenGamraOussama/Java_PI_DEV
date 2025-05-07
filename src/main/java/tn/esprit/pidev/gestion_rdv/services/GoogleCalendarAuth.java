package tn.esprit.pidev.gestion_rdv.services;



import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoogleCalendarAuth {

    private static final String CLIENT_SECRET_FILE = "/org/example/pi__dev_/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + ".javapidev" + File.separator + "tokens";
    static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final FileDataStoreFactory DATA_STORE_FACTORY;

    static {
        try {
            // Suppress the warning about permissions on Windows
            // by setting the logger level to OFF for FileDataStoreFactory
            Logger logger = Logger.getLogger(FileDataStoreFactory.class.getName());
            logger.setLevel(Level.OFF);

            // Create the tokens directory if it doesn't exist
            File tokensDir = new File(TOKENS_DIRECTORY_PATH);
            if (!tokensDir.exists()) {
                if (!tokensDir.mkdirs()) {
                    System.err.println("Warning: Failed to create tokens directory: " + TOKENS_DIRECTORY_PATH);
                }
            }
            DATA_STORE_FACTORY = new FileDataStoreFactory(tokensDir);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing DATA_STORE_FACTORY", e);
        }
    }

    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String APPLICATION_NAME = "Java Calendar API Integration";

    public static Credential getCredentials() throws IOException {
        // Load client secrets
        InputStream in = GoogleCalendarAuth.class.getResourceAsStream(CLIENT_SECRET_FILE);
        if (in == null) {
            throw new IOException("Resource not found: " + CLIENT_SECRET_FILE);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Check if client ID is a placeholder or null
        String clientId = null;

        // Try to get client ID from details (for "web" or "installed" format)
        if (clientSecrets.getDetails() != null) {
            clientId = clientSecrets.getDetails().getClientId();
        }

        // If that fails, try to get it from the "web" section if it exists
        if (clientId == null && clientSecrets.getWeb() != null) {
            clientId = clientSecrets.getWeb().getClientId();
        }

        // If that fails, try to get it from the "installed" section if it exists
        if (clientId == null && clientSecrets.getInstalled() != null) {
            clientId = clientSecrets.getInstalled().getClientId();
        }

        if (clientId == null || clientId.equals("YOUR_CLIENT_ID")) {
            System.err.println("Warning: Using placeholder client ID. Please replace 'YOUR_CLIENT_ID' with your actual Google API client ID in the client secret file.");
            throw new IOException("Invalid client ID in client secret file. Please replace 'YOUR_CLIENT_ID' with your actual Google API client ID.");
        }

        // Check if client secret is a placeholder or null
        String clientSecret = null;

        // Try to get client secret from details (for "web" or "installed" format)
        if (clientSecrets.getDetails() != null) {
            clientSecret = clientSecrets.getDetails().getClientSecret();
        }

        // If that fails, try to get it from the "web" section if it exists
        if (clientSecret == null && clientSecrets.getWeb() != null) {
            clientSecret = clientSecrets.getWeb().getClientSecret();
        }

        // If that fails, try to get it from the "installed" section if it exists
        if (clientSecret == null && clientSecrets.getInstalled() != null) {
            clientSecret = clientSecrets.getInstalled().getClientSecret();
        }

        if (clientSecret == null || clientSecret.equals("YOUR_CLIENT_SECRET")) {
            System.err.println("Warning: Using placeholder client secret. Please replace 'YOUR_CLIENT_SECRET' with your actual Google API client secret in the client secret file.");
            throw new IOException("Invalid client secret in client secret file. Please replace 'YOUR_CLIENT_SECRET' with your actual Google API client secret.");
        }

        // Build the authorization flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();

        // Authorize
        // Use port 0 to let the system pick an available port
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(0).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
