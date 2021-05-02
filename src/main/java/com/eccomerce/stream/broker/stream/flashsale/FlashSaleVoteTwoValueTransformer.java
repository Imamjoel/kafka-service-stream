package com.eccomerce.stream.broker.stream.flashsale;


import com.eccomerce.stream.broker.message.FlashSaleVoteMessage;
import com.eccomerce.stream.util.LocalDateTimeUtil;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.time.LocalDateTime;

public class FlashSaleVoteTwoValueTransformer implements ValueTransformer<FlashSaleVoteMessage, FlashSaleVoteMessage> {

    private final long voteStartTime;
    private final long voteEndTime;
    private ProcessorContext processorContext;

    public FlashSaleVoteTwoValueTransformer(LocalDateTime voteStart, LocalDateTime voteEnd) {
        this.voteStartTime = LocalDateTimeUtil.toEpochTimestamp(voteStart);
        this.voteEndTime = LocalDateTimeUtil.toEpochTimestamp(voteEnd);
    }


    @Override
    public void init(ProcessorContext context) {
        this.processorContext = context;
    }


    @Override
    public FlashSaleVoteMessage transform(FlashSaleVoteMessage value) {
        var recordTime = processorContext.timestamp();

        return (recordTime >= voteStartTime && recordTime <= voteEndTime) ? value : null;
    }


    @Override
    public void close() {

    }
}
