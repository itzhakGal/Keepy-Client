package com.example.keepy.app.activity.kindergartenScreen.history;

public class Alert {

    private String event;
    private String timestamp;
    private String word;

    public Alert() {
        // Default constructor required for calls to DataSnapshot.getValue(Alert.class)
    }

    public Alert(String event, String timestamp, String word) {
        this.event = event;
        this.timestamp = timestamp;
        this.word = word;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
