package com.eccomerce.stream.broker.stream.commodity;

import com.eccomerce.stream.broker.message.OrderMessage;
import com.eccomerce.stream.util.CommodityStreamUtil;
import netscape.javascript.JSObject;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class MaskOrderStream {

    public KStream<String, OrderMessage> kstreamCommodityTrading(StreamsBuilder builder) {
        var stringSerde = Serdes.String();
        var orderSerde = new JsonSerde<>(OrderMessage.class);

        KStream<String, OrderMessage> orderStream = builder.stream("t.commodity.order", Consumed.with(stringSerde, orderSerde))
                .mapValues(CommodityStreamUtil::maskCreditCard);

        orderStream.to("t.commodity.order-musk", Produced.with(stringSerde, orderSerde));
        orderStream.print(Printed.<String, OrderMessage>toSysOut().withLabel("Masked OrderStream"));

        return  orderStream;

    }
}
