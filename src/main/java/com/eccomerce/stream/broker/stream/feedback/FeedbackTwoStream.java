package com.eccomerce.stream.broker.stream.feedback;

import com.eccomerce.stream.broker.message.FeedbackMessage;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;


import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Configuration
public class FeedbackTwoStream {
    private static final Set<String> GOOD_WORD = Set.of("happy", "good", "helpful");

    @Bean
    public KStream<String, String> kstreamFeedback(StreamsBuilder builder) {
        var stringSerde = Serdes.String();
        var feedbackSerde = new JsonSerde<>(FeedbackMessage.class);

        var goodFeedbackStream = builder.stream("t.commodity.feedback", Consumed.with(stringSerde, feedbackSerde))
                .flatMap((k, v) -> Arrays.asList(v.getFeedback().replaceAll("[^a-zA-Z]", "")
                        .toLowerCase().split("\\s+")).stream().filter(word -> GOOD_WORD.contains(word)).distinct()
                        .map(goodWord -> KeyValue.pair(v.getLocation(), goodWord)).collect(toList()));

        goodFeedbackStream.to("t.commodity.feedback-two-good");

        return goodFeedbackStream;
    }

    private ValueMapper<FeedbackMessage, Iterable<String>> mapperGoodWords() {
        return feedbackMessage -> Arrays.asList(feedbackMessage.getFeedback().replaceAll("[^a-zA-Z]", "")
                .toLowerCase().split("\\s+")).stream().filter(word -> GOOD_WORD.contains(word)).distinct().collect(toList());
    }
}
