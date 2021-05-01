package com.eccomerce.stream.broker.message;

import com.eccomerce.stream.util.LocalDateTimeDeserializer;
import com.eccomerce.stream.util.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public class FeedbackMessage {
    private String feedback;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime feedbackDateTime;

    private String location;

    private int rating;

    public String getFeedback() {
        return feedback;
    }

    public LocalDateTime getFeedbackDateTime() {
        return feedbackDateTime;
    }

    public String getLocation() {
        return location;
    }

    public int getRating() {
        return rating;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setFeedbackDateTime(LocalDateTime feedbackDateTime) {
        this.feedbackDateTime = feedbackDateTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "FeedbackMessage [feedback=" + feedback + ", feedbackDateTime=" + feedbackDateTime + ", location="
                + location + ", rating=" + rating + "]";
    }
}
