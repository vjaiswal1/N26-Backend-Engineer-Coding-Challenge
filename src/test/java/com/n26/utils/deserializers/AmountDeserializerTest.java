package com.n26.utils.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.n26.models.Transaction;
import com.n26.utils.deserializers.AmountDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
public class AmountDeserializerTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BigDecimal.class, new AmountDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void testValidAmount() throws IOException {
        Transaction transaction = objectMapper.readValue(createTransaction("89.64737"), Transaction.class);
        assertEquals(new BigDecimal("89.64737"), transaction.getAmount());
    }

    @Test
    public void testInvalidAmount() throws IOException {
        Transaction transaction = objectMapper.readValue(createTransaction("random amount"), Transaction.class);
        assertNull(transaction.getAmount());
    }

    private String createTransaction(final String amount) {
        JSONObject transaction = new JSONObject();
        try {
            transaction.put("amount", amount);
        } catch (JSONException e) {
        }
        return transaction.toString();
    }

}