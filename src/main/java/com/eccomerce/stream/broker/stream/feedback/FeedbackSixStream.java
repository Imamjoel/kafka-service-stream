package com.eccomerce.stream.broker.stream.feedback;

import com.eccomerce.stream.broker.message.FeedbackMessage;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Configuration
public class FeedbackSixStream {
    private static final Set<String> GOOD_WORD = Set.of("happy", "good", "helpful");
    private static final Set<String> BAD_WORD = Set.of("angry", "sad", "bad");

    @Bean
    public KStream<String, FeedbackMessage> kstreamFeedback(StreamsBuilder builder) {
        var stringSerde = Serdes.String();
        var feedbackSerde = new JsonSerde<>(FeedbackMessage.class);

        var sourceStream = builder.stream("t.commodity.feedback", Consumed.with(stringSerde, feedbackSerde));
        var feedbackStream = sourceStream.flatMap(splitWord()).branch(isGoodWord(), isBadWord());
        feedbackStream[0].through("t.commodity.feedback-six-good-count").groupByKey().count().toStream().
                to("t.commodity.feedback-six-good-count");

        feedbackStream[0].through("t.commodity.feedback-six-bad-count").groupByKey().count().toStream()
                .to("t.commodity.feedback-six-bad-count");

        // addition requaired
        feedbackStream[0].groupBy((key, value) -> value).count().toStream().to("t.commodity.feedback-six-good-word");
        feedbackStream[1].groupBy((key, value) -> value).count().toStream().to("t.commodity.feedback-six-bad-word");

        return sourceStream;
    }

    private Predicate<? super String,? super String> isBadWord() {
        return (key, value) -> BAD_WORD.contains(value);
    }

    private Predicate<? super String, ? super String> isGoodWord() {
        return ((key, value) -> GOOD_WORD.contains(value));
    }

    private KeyValueMapper<String, FeedbackMessage, Iterable<KeyValue<String, String>>> splitWord() {
        return (key, value) -> Arrays.asList(value.getFeedback().replaceAll("[^a-zA-Z]", "")
                .toLowerCase().split("\\s+")).stream()
                .distinct()
                .map(word -> KeyValue.pair(value.getLocation(), word)).collect(toList());

    }

}
