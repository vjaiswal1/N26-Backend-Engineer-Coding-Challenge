package com.n26.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Deserializer for amount in transaction json requests
 */
@Component
public class AmountDeserializer extends JsonDeserializer {

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        final String bigDecimal = jsonParser.getText();
        try {
            return new BigDecimal(bigDecimal);
        } catch (NumberFormatException e) {
            return null;
        }

    }
}
