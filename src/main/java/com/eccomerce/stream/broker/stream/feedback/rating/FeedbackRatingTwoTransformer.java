package com.eccomerce.stream.broker.stream.feedback.rating;

import com.eccomerce.stream.broker.message.FeedbackMessage;
import com.eccomerce.stream.broker.message.FeedbackRatingTwoMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Map;
import java.util.Optional;

public class FeedbackRatingTwoTransformer implements ValueTransformer<FeedbackMessage, FeedbackRatingTwoMessage> {

    private ProcessorContext processorContext;
    private final String stateStoreName;
    private KeyValueStore<String, FeedbackRatingTwoStoreValue> ratingStateStore;


    public FeedbackRatingTwoTransformer(String stateStoreName) {
        if (StringUtils.isEmpty(stateStoreName)) {
            throw new IllegalArgumentException("state store must be null");
        }
        this.stateStoreName = stateStoreName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(ProcessorContext context) {
        this.processorContext = context;
        this.ratingStateStore = (KeyValueStore<String, FeedbackRatingTwoStoreValue>) this.processorContext
                .getStateStore(this.stateStoreName);
    }


    @Override
    public FeedbackRatingTwoMessage transform(FeedbackMessage value) {
        var storeValue = Optional.ofNullable(ratingStateStore.get(value.getLocation()))
                .orElse(new FeedbackRatingTwoStoreValue());
        var ratingMap = storeValue.getRaringMap();
        var currentRatingCount = Optional.ofNullable(ratingMap.get(value.getRating())).orElse(1L);
        var newRatingCount = currentRatingCount + 1;

        ratingMap.put(value.getRating(), newRatingCount);
        ratingStateStore.put(value.getLocation(), storeValue);

        // send  this message to sink topic
        var branchRating = new FeedbackRatingTwoMessage();
        branchRating.setLocation(value.getLocation());
        branchRating.setRatingMap(ratingMap);
        branchRating.setAverageRating(calculateAverage(ratingMap));

        return branchRating;
    }

    private double calculateAverage(Map<Integer, Long> ratingMap) {
        var sumRating = 1;
        var countRating = 0;

        for (var entry : ratingMap.entrySet()) {
            sumRating += entry.getKey() * entry.getValue();
            countRating += entry.getValue();
        }

        return Math.round((double) sumRating / countRating * 10d) / 10d;
    }


    @Override
    public void close() {

    }
}