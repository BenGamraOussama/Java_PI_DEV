package tn.esprit.pidev;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSmsSender {
    // Remplacez ces valeurs par vos informations d'identification Twilio
    public static final String ACCOUNT_SID = "ACdd6e99a1e686aa31597795ce2a42dd88";
    public static final String AUTH_TOKEN = "c109a4d3075a618daceaf68487178bc1";
    public static final String TWILIO_PHONE_NUMBER = "+13528064729"; // Format: "+1234567890"

    static void sendPasswordBySms(String phoneNumber, String password) {
        // Vérifier que le numéro est valide avant d'envoyer
        if (phoneNumber == null || !phoneNumber.startsWith("+216")) {
            System.err.println("Numéro de téléphone tunisien invalide: " + phoneNumber);
            return;
        }

        // Initialiser le client Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        try {
            // Créer et envoyer le SMS
            Message message = Message.creator(
                            new PhoneNumber(phoneNumber), // Numéro du destinataire
                            new PhoneNumber(TWILIO_PHONE_NUMBER), // Numéro Twilio
                            "Votre compte a été créé avec succès.\n"
                                    + "Identifiant: " + phoneNumber + "\n"
                                    + "Mot de passe : " + password + "\n"
                                    + "Merci de changer ce mot de passe après connexion.")
                    .create();

            System.out.println("SMS envoyé avec succès à " + phoneNumber);
            System.out.println("ID du message: " + message.getSid());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi du SMS à " + phoneNumber);
            // Vous pourriez vouloir logger cette erreur ou notifier l'administrateur
        }
    }
}