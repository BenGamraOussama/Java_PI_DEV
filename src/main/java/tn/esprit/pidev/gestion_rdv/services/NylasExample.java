package tn.esprit.pidev.gestion_rdv.services;
import com.nylas.NylasClient;
import com.nylas.models.Calendar;
import com.nylas.models.ListCalendersQueryParams;
import com.nylas.models.ListResponse;

public class NylasExample {
    public static void main(String[] args) {
        String apiKey = "nyk_v0_jGwIBZX88Lp0Zme1dfufxVJ4FnRiY8jKt0IshJPnSWq7GCdx2jNKdZb1YWH8vCVj";
        String grantId = "7206a16f-8649-49a7-80cc-c365c665ae26";  // Remplacez par le grant ID de votre application

        NylasClient client = new NylasClient.Builder(apiKey).build();

        try {
            // Créez un objet ListCalendersQueryParams si vous souhaitez filtrer les résultats
            ListCalendersQueryParams queryParams = new ListCalendersQueryParams();

            // Récupérez les calendriers en utilisant le grantId
            ListResponse<Calendar> calendars = client.calendars().list(grantId, queryParams);

            // Affichez les noms des calendriers
            calendars.getData().forEach(calendar ->
                    System.out.println("Nom du calendrier : " + calendar.getName())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

