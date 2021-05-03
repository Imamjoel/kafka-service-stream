package com.eccomerce.stream.broker.message;

public class FeedbackRatingMessage {
    private String location;
    private double averageRating;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    @Override
    public String toString() {
        return "FeedbackRatingMessage{" +
                "location='" + location + '\'' +
                ", averageRating=" + averageRating +
                '}';
    }
}
