package tn.esprit.pidev.gestion_rdv.services;

/**
 * Represents a Zoom meeting with its properties.
 */
public class Meeting {
    private String topic;
    private String startTime;
    private int duration;
    private String timezone;
    private String password;
    private String joinUrl;

    public Meeting() {
        // Default constructor
    }

    /**
     * Gets the topic of the meeting.
     * @return The meeting topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Sets the topic of the meeting.
     * @param topic The meeting topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Gets the start time of the meeting in ISO format.
     * @return The start time
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the meeting in ISO format.
     * @param startTime The start time
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the duration of the meeting in minutes.
     * @return The duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the meeting in minutes.
     * @param duration The duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Gets the timezone of the meeting.
     * @return The timezone
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Sets the timezone of the meeting.
     * @param timezone The timezone
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /**
     * Gets the password of the meeting.
     * @return The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the meeting.
     * @param password The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the join URL of the meeting.
     * @return The join URL
     */
    public String getJoinUrl() {
        return joinUrl;
    }

    /**
     * Sets the join URL of the meeting.
     * @param joinUrl The join URL
     */
    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }
}