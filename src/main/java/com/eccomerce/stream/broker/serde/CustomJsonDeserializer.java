package com.eccomerce.stream.broker.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Objects;

public class CustomJsonDeserializer<T> implements Deserializer<T> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> deserializedClass;

    public CustomJsonDeserializer(Class<T> deserializedClass) {
        Objects.requireNonNull(deserializedClass, "Deserialized class not null");
        this.deserializedClass = deserializedClass;
    }
    /**
     * Deserialize a record value from a byte array into a value or object.
     *
     * @param topic topic associated with the data
     * @param data  serialized bytes; may be null; implementations are recommended to handle null by returning a value or null rather than throwing an exception.
     * @return deserialized typed data; may be null
     */
    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, deserializedClass);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
