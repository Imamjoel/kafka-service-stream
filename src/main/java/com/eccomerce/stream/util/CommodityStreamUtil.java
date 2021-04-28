package com.eccomerce.stream.util;

import com.eccomerce.stream.broker.message.OrderMessage;
import com.eccomerce.stream.broker.message.OrderPatternMessage;
import com.eccomerce.stream.broker.message.OrderRewardMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.kstream.Predicate;

public class CommodityStreamUtil {

    public static OrderMessage maskCreditCard(OrderMessage original) {
        var converter = original.copy();
        var maskCreditCardNumber = original.getCreditCardNumber().replaceFirst("\\d{12}", StringUtils.repeat("*", 12));

        converter.setCreditCardNumber(maskCreditCardNumber);
        return converter;
    }

    public static OrderPatternMessage mapToOrderPattern(OrderMessage message) {
        var result = new OrderPatternMessage();

        result.setItemName(message.getItemName());
        result.setOrderDateTime(message.getOrderDateTime());
        result.setOrderLocation(message.getOrderLocation());
        result.setOrderNumber(message.getOrderNumber());

        var totalItemAmount = message.getPrice() * message.getQuantity();
        result.setTotalItemAmount(totalItemAmount);

        return result;
    }

    public static OrderRewardMessage mapToOrderReward(OrderMessage message) {
        var result = new OrderRewardMessage();

        result.setItemName(message.getItemName());
        result.setOrderDateTime(message.getOrderDateTime());
        result.setOrderLocation(message.getOrderLocation());
        result.setOrderNumber(message.getOrderNumber());
        result.setPrice(message.getPrice());
        result.setQuantity(message.getQuantity());

        return result;
    }

    public static Predicate<String, OrderMessage> isLargeQuantity() {
        return (key, value) -> value.getQuantity() > 200;
    }
}
