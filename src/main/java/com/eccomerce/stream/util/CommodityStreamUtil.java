package com.eccomerce.stream.util;

import com.eccomerce.stream.broker.message.OrderMessage;
import org.apache.commons.lang3.StringUtils;

public class CommodityStreamUtil {

    public static OrderMessage maskCreditCard(OrderMessage original) {
        var converter = original.copy();
        var maskCreditCardNumber = original.getCreditCardNumber().replaceFirst("\\d{12}", StringUtils.repeat("*", 12));

        converter.setCreditCardNumber(maskCreditCardNumber);
        return converter;
    }

}
