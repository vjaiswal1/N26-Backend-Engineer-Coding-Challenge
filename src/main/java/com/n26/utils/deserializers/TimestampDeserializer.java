package com.n26.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Deserializer for timestamp in transaction json requests
 */
@Component
public class TimestampDeserializer extends JsonDeserializer {
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        final String timestamp = jsonParser.getText();
        try {
            return formatter.parse(timestamp).getTime();
        } catch (ParseException e) {
            return null;
        }

    }
}
