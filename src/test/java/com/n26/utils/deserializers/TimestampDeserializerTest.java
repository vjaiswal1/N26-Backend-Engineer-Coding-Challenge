package com.n26.utils.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.n26.models.Transaction;
import com.n26.utils.deserializers.TimestampDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class TimestampDeserializerTest {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of("UTC"));

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Long.class, new TimestampDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void testValidTimestamp() throws IOException {
        final Instant instant = Instant.now();
        Transaction transaction = objectMapper.readValue(createTransaction(getTimestamp(instant)), Transaction.class);
        assertEquals((Long) instant.toEpochMilli(), transaction.getTimestamp());
    }

    @Test
    public void testInvalidTimestamp() throws IOException {
        Transaction transaction = objectMapper.readValue(createTransaction("random timestamp"), Transaction.class);
        assertNull(transaction.getTimestamp());
    }

    private String createTransaction(final String timestamp) {
        JSONObject transaction = new JSONObject();
        try {
            transaction.put("timestamp", timestamp);
        } catch (JSONException e) {
        }
        return transaction.toString();
    }

    private String getTimestamp(final Instant instant) {
        return TIMESTAMP_FORMATTER.format(instant);
    }
}
