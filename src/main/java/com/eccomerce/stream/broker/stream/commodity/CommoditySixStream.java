package com.eccomerce.stream.broker.stream.commodity;

import com.eccomerce.stream.broker.message.OrderMessage;
import com.eccomerce.stream.broker.message.OrderPatternMessage;
import com.eccomerce.stream.broker.message.OrderRewardMessage;
import com.eccomerce.stream.util.CommodityStreamUtil;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.kafka.support.serializer.JsonSerde;

import static com.eccomerce.stream.util.CommodityStreamUtil.*;

@Configuration
public class CommoditySixStream {

    public KStream<String, OrderMessage> kStreamCommodityTrading(StreamsBuilder builder) {
        var stringSerde = Serdes.String();
        var orderSerde = new JsonSerde<>(OrderMessage.class);
        var orderPatternSerde = new JsonSerde<>(OrderPatternMessage.class);
        var orderRewardSerde = new JsonSerde<>(OrderRewardMessage.class);

        KStream<String, OrderMessage> maskedOrderStream = builder.stream("t.commodity.order", Consumed.with(stringSerde, orderSerde))
                .mapValues(CommodityStreamUtil::maskCreditCard);

        // 1st sink stream to pattern
        final var branchProducer = Produced.with(stringSerde, orderPatternSerde);

        new KafkaStreamBrancher<String, OrderPatternMessage>()
                .branch(isPlastic(), kstream -> kstream.to("t.commodity.pattern-five.plastic", branchProducer))
                .defaultBranch(kstream -> kstream.to("t.commodity.pattern-five.notplastic", branchProducer))
                .onTopOf(maskedOrderStream.mapValues(CommodityStreamUtil::mapToOrderPattern));

        // 2nd sink stream to reward
        // filter only "large" quantity
        KStream<String, OrderRewardMessage> rewardStream = maskedOrderStream.filter(isLargeQuantity())
                .filterNot(isCheap())
                .map(CommodityStreamUtil.mapToOrderRewardChangeKey());
        rewardStream.to("t.commodity.reward-five", Produced.with(stringSerde, orderRewardSerde));

        // 3rd sink to stream to storage
        // no transformation
        KStream<String, OrderMessage> storageStream = maskedOrderStream.selectKey(generateStoragKey());
        storageStream.to("t.commodity.storage-five", Produced.with(stringSerde, orderSerde));

        // 4th stream for fraud
        KStream<String, OrderMessage> fraudStream = maskedOrderStream.filter((k, v) -> v.getOrderLocation().toUpperCase().startsWith("C"))
                .peek((k, v) -> this.reportFraud(v));
        fraudStream.map((k, v) -> KeyValue.pair(v.getOrderLocation().toUpperCase().charAt(0) + "***", v.getPrice() * v.getQuantity()))
                .to("t.commodity.fraud-six", Produced.with(stringSerde, Serdes.Integer()));


        return maskedOrderStream;

    }

    private static final Logger LOG = LoggerFactory.getLogger(CommoditySixStream.class);

    private void reportFraud(OrderMessage orderMessage) {
        LOG.info("Reporting fraud {}", orderMessage);
    }

}
