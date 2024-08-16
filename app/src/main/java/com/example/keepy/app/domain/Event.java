package com.example.keepy.app.domain;

public class Event {
    private String eventType;
    private String description;
    private String dateTime;
    private String id;

    // Empty constructor for Firebase
    public Event() {
    }

    public Event(String eventType, String description, String dateTime, String id) {
        this.eventType = eventType;
        this.description = description;
        this.dateTime = dateTime;
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getId() {
        return id;
    }
}
