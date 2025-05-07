package tn.esprit.pidev.gestion_activite.services;

import tn.esprit.pidev.gestion_activite.entities.Activite;
import tn.esprit.pidev.gestion_activite.entities.Exercice;
import tn.esprit.pidev.gestion_activite.entities.Patient;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class EmailService {
    private static final String GMAIL_USERNAME = "oussemadenguir999@gmail.com";
    private static final String GMAIL_PASSWORD = "ttxnozvuxteacmfy";
    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final int SMTP_PORT = 465;

    public void sendNewActivityEmail(Patient patient, Activite activite) throws IOException {
        String subject = "Nouvelle activité assignée";
        String content = "<h2>Bonjour " + patient.getPrenom() + " " + patient.getNom() + ",</h2>" +
                "<p>Une nouvelle activité vous a été assignée :</p>" +
                "<h3>" + activite.getTitre() + "</h3>" +
                "<p>Description : " + activite.getDescription() + "</p>" +
                "<p>Type : " + activite.getType() + "</p>" +
                "<p>Connectez-vous à votre compte pour commencer cette activité.</p>" +
                "<p>Cordialement,<br>L'équipe de suivi</p>";

        sendEmail(patient.getEmail(), subject, content);
    }

    public void sendNewExerciseEmail(Patient patient, Exercice exercice) throws IOException {
        String subject = "Nouvel exercice assigné";
        String content = "<h2>Bonjour " + patient.getPrenom() + " " + patient.getNom() + ",</h2>" +
                "<p>Un nouvel exercice vous a été assigné :</p>" +
                "<h3>" + exercice.getActivite().getTitre() + "</h3>" +
                "<p>Question : " + exercice.getQuestion() + "</p>" +
                "<p>Connectez-vous à votre compte pour répondre à cet exercice.</p>" +
                "<p>Cordialement,<br>L'équipe de suivi</p>";

        sendEmail(patient.getEmail(), subject, content);
    }

    private void sendEmail(String toEmail, String subject, String content) throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (SSLSocket socket = (SSLSocket) factory.createSocket(SMTP_SERVER, SMTP_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            // Read welcome message
            readResponse(in);

            // Send EHLO
            sendCommand(out, "EHLO localhost");
            readResponse(in);

            // Authenticate
            sendCommand(out, "AUTH LOGIN");
            readResponse(in);
            sendCommand(out, java.util.Base64.getEncoder().encodeToString(GMAIL_USERNAME.getBytes(StandardCharsets.UTF_8)));
            readResponse(in);
            sendCommand(out, java.util.Base64.getEncoder().encodeToString(GMAIL_PASSWORD.getBytes(StandardCharsets.UTF_8)));
            readResponse(in);

            // Set sender and recipient
            sendCommand(out, "MAIL FROM: <" + GMAIL_USERNAME + ">");
            readResponse(in);
            sendCommand(out, "RCPT TO: <" + toEmail + ">");
            readResponse(in);

            // Send email data
            sendCommand(out, "DATA");
            readResponse(in);

            // Send email headers and content
            out.println("From: " + GMAIL_USERNAME);
            out.println("To: " + toEmail);
            out.println("Subject: " + subject);
            out.println("MIME-Version: 1.0");
            out.println("Content-Type: text/html; charset=UTF-8");
            out.println();
            out.println(content);
            out.println(".");

            // End email
            readResponse(in);

            // Quit
            sendCommand(out, "QUIT");
            readResponse(in);
        }
    }

    private void sendCommand(PrintWriter out, String command) {
        out.println(command);
    }

    private void readResponse(BufferedReader in) throws IOException {
        String line;
        do {
            line = in.readLine();
            if (line == null) {
                throw new IOException("Connection closed by server");
            }
            if (line.startsWith("5")) {
                throw new IOException("SMTP error: " + line);
            }
        } while (line.charAt(3) == '-');
    }
} 