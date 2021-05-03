package com.eccomerce.stream.broker.stream.feedback.rating;

import com.eccomerce.stream.broker.message.FeedbackMessage;
import com.eccomerce.stream.broker.message.FeedbackRatingMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Optional;

public class FeedbackRatingOneTransformer implements ValueTransformer<FeedbackMessage, FeedbackRatingMessage> {

    private ProcessorContext processorContext;
    private final String stateStoreName;
    private KeyValueStore<String, FeedbackRatingOneStoreValue> ratingStore;

    public FeedbackRatingOneTransformer(String stateStoreName) {
        if (StringUtils.isEmpty(stateStoreName)) {
            throw new IllegalArgumentException("state store name must not empty");
        }
        this.stateStoreName = stateStoreName;

    }


    @SuppressWarnings("unchecked")
    @Override
    public void init(ProcessorContext context) {
        this.processorContext = context;
        this.ratingStore = (KeyValueStore<String, FeedbackRatingOneStoreValue>) this.processorContext
                .getStateStore(stateStoreName);
    }


    @Override
    public FeedbackRatingMessage transform(FeedbackMessage value) {
        var storeValue = Optional.ofNullable(ratingStore.get(value.getLocation()))
                .orElse(new FeedbackRatingOneStoreValue());

        // update new store
        var newSumRating = storeValue.getSumRating() + value.getRating();
        storeValue.setSumRating(newSumRating);
        var newCountRating = storeValue.getCountRating() + 1;
        storeValue.setCountRating(newCountRating);

        ratingStore.put(value.getLocation(), storeValue);

        // build branch rating
        var branchRating = new FeedbackRatingMessage();
        branchRating.setLocation(value.getLocation());
        double averageRating = Math.round( (double) newSumRating / newCountRating * 10d) / 10d;
        branchRating.setAverageRating(averageRating);

        return branchRating;
    }


    @Override
    public void close() {

    }
}
