package com.eccomerce.stream.broker.serde;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;

public class CustomJsonSerializer<T> implements Serializer<T> {

    private ObjectMapper objectMapper = new ObjectMapper();



    /**
     * Convert {@code data} into a byte array.
     *
     * @param topic topic associated with data
     * @param data  typed data
     * @return serialized bytes
     */
    @Override
    public byte[] serialize(String topic, T data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException();
        }
    }
}
