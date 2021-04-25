package com.n26.utils.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serializer that converts a BigDecimal to json string
 */
@Component
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

    @Value("${statistics.decimal.scale}")
    private int decimalScale;

    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeString(bigDecimal.setScale(decimalScale, RoundingMode.HALF_UP).toString());
    }
}
