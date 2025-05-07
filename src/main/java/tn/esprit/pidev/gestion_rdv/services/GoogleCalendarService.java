package tn.esprit.pidev.gestion_rdv.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.CalendarScopes;
import tn.esprit.pidev.gestion_rdv.enteties.RDV;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Java Calendar API Integration";
    private static final String CLIENT_SECRET_FILE = "credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static Calendar calendarService;

    // Initialize Google Calendar service
    public static Calendar getCalendarService() throws IOException {
        if (calendarService == null) {
            Credential credential = GoogleCalendarAuth.getCredentials(); // Fetch credentials using OAuth2
            calendarService = new Calendar.Builder(GoogleCalendarAuth.HTTP_TRANSPORT, GoogleCalendarAuth.JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
        return calendarService;
    }

    // Method to create an event in Google Calendar
    public static Event createEvent(RDV rdv) throws IOException {
        Event event = new Event()
                .setSummary("Rendez-vous: " + rdv.getPriorite())
                .setDescription("Rendez-vous details: " + rdv.getPriorite())
                .setStart(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(rdv.getDate().getTime())))
                .setEnd(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(rdv.getDate().getTime() + 3600000))); // Default 1 hour duration

        // Add custom property to link with RDV ID
        event.set("rdvId", rdv.getId());

        String calendarId = "primary"; // Use the primary calendar
        Event createdEvent = calendarService.events().insert(calendarId, event).execute();
        System.out.println("Event created: " + createdEvent.getHtmlLink());
        return createdEvent;
    }

    // Method to update an event in Google Calendar
    public static Event updateEvent(String eventId, RDV rdv) throws IOException {
        // First get the event
        String calendarId = "primary";
        Event event = calendarService.events().get(calendarId, eventId).execute();

        // Update the event details
        event.setSummary("Rendez-vous: " + rdv.getPriorite())
             .setDescription("Rendez-vous details: " + rdv.getPriorite())
             .setStart(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(rdv.getDate().getTime())))
             .setEnd(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(rdv.getDate().getTime() + 3600000)));

        // Update the event
        Event updatedEvent = calendarService.events().update(calendarId, eventId, event).execute();
        System.out.println("Event updated: " + updatedEvent.getHtmlLink());
        return updatedEvent;
    }

    // Method to delete an event from Google Calendar
    public static void deleteEvent(String eventId) throws IOException {
        String calendarId = "primary";
        calendarService.events().delete(calendarId, eventId).execute();
        System.out.println("Event deleted: " + eventId);
    }

    // Method to find an event by RDV ID
    public static String findEventIdByRdvId(int rdvId) throws IOException {
        String calendarId = "primary";
        Events events = calendarService.events().list(calendarId)
                .setMaxResults(100)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        for (Event event : events.getItems()) {
            Object eventRdvId = event.get("rdvId");
            if (eventRdvId != null && eventRdvId.toString().equals(String.valueOf(rdvId))) {
                return event.getId();
            }
        }

        return null; // Event not found
    }

    // Method to get events from Google Calendar
    public static List<Event> getEvents() throws IOException {
        String calendarId = "primary";
        Events events = calendarService.events().list(calendarId)
                .setMaxResults(10)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }
}
