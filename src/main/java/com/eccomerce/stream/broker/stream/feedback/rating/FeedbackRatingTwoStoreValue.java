package com.eccomerce.stream.broker.stream.feedback.rating;

import java.util.Map;
import java.util.TreeMap;

public class FeedbackRatingTwoStoreValue {

    private Map<Integer, Long> raringMap = new TreeMap<>();

    public Map<Integer, Long> getRaringMap() {
        return raringMap;
    }

    public void setRaringMap(Map<Integer, Long> raringMap) {
        this.raringMap = raringMap;
    }
}
