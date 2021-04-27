package com.eccomerce.stream.broker.stream.prommotion;

import com.eccomerce.stream.broker.message.PromotionMessage;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class PromotionUppercaseSpringJsonSpring {

    @Bean
    public KStream<String, PromotionMessage> kStreamPromotion(StreamsBuilder builder) {
        var stringSerde = Serdes.String();
        var jsonSerde = new JsonSerde<>(PromotionMessage.class);

        KStream<String, PromotionMessage> sourceStream = builder.stream("t.commodity.promotion", Consumed.with(stringSerde, jsonSerde));
        KStream<String, PromotionMessage> uppercaseStream = sourceStream.mapValues(this::upperCasePromotionCode);

        uppercaseStream.to("t.commodity.promotion", Produced.with(stringSerde, jsonSerde));
        sourceStream.print(Printed.<String, PromotionMessage>toSysOut().withLabel("JSON Serde Original Stream"));
        uppercaseStream.print(Printed.<String, PromotionMessage>toSysOut().withLabel("JSON Serde Uppercase Stream"));

        return sourceStream;

    }

    private PromotionMessage upperCasePromotionCode(PromotionMessage message) {
        return new PromotionMessage(message.getPromotionCode().toUpperCase());
    }

}
