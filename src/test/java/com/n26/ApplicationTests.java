package com.n26;


import com.n26.Application;
import com.n26.cache.StatisticsCache;
import com.n26.models.Transaction;
import com.n26.services.TransactionService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of("UTC"));

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    @Qualifier("statisticsMapCache")
    private StatisticsCache statisticsCache;

    @Autowired
    private TransactionService transactionServiceImpl;

    @Value("${aggregator.window.size}")
    private int aggregatorWindowSize;

    private MockMvc mvc;

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        statisticsCache.clear();
    }

    @Test
    public void testInvalidJson() throws Exception {

        this.mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andReturn();
        checkEmptyStatistics();
    }

    @Test
    public void testInvalidDate() throws Exception {

        this.mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction("67.98", "random timestamp")))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();
        checkEmptyStatistics();
    }

    @Test
    public void testInvalidAmount() throws Exception {

        this.mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction("random amount", getTimestamp(0))))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();
        checkEmptyStatistics();
    }

    @Test
    public void testFutureDate() throws Exception {

        this.mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction("1000.99", getTimestamp(10000))))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();
        checkEmptyStatistics();
    }

    @Test
    public void testExpiredDate() throws Exception {

        this.mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction("100.78", getTimestamp(-aggregatorWindowSize - 1000))))
                .andExpect(status().isNoContent())
                .andReturn();
        checkEmptyStatistics();
    }

//    @Test
//    public void givenMultiThread_whenNonSyncMethod() {
//        ExecutorService service = Executors.newFixedThreadPool(2);
//        Transaction t= getStubbedTransaction(BigDecimal.ONE,
//                Instant.now().toEpochMilli());
//
//
//        IntStream.range(0, 100)
//                .forEach(count -> service.submit(transactionServiceImpl.add(t)));
//        try {
//            service.awaitTermination(1000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
////        assertEquals(1000, summation.getSum());
//    }

    private Transaction getStubbedTransaction(final BigDecimal amount, final Long timestamp) {
        final Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTimestamp(timestamp);
        return transaction;
    }
    @Test
    public void testValidPostCase() throws Exception {
        this.mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction("579.90897", getTimestamp(0))))
                .andExpect(status().isCreated())
                .andReturn();
        this.mvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.max", is("579.91")))
                .andExpect(jsonPath("$.min", is("579.91")))
                .andExpect(jsonPath("$.avg", is("579.91")))
                .andExpect(jsonPath("$.sum", is("579.91")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testValidDeleteCase() throws Exception {
        this.mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction("08.90897", getTimestamp(0))))
                .andExpect(status().isCreated())
                .andReturn();
        this.mvc.perform(delete("/transactions"))
                .andExpect(status().isNoContent()).andReturn();
        checkEmptyStatistics();

    }

    @Test
    public void testValidNestedCase() throws Exception {
        this.mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction("10.0000001", getTimestamp(0))))
                .andExpect(status().isCreated())
                .andReturn();
        this.mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction("100.000001", getTimestamp(0))))
                .andExpect(status().isCreated())
                .andReturn();
        this.mvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.count", is(2)))
                .andExpect(jsonPath("$.max", is("100.00")))
                .andExpect(jsonPath("$.min", is("10.00")))
                .andExpect(jsonPath("$.avg", is("55.00")))
                .andExpect(jsonPath("$.sum", is("110.00")))
                .andExpect(status().isOk())
                .andReturn();
        this.mvc.perform(delete("/transactions"))
                .andExpect(status().isNoContent()).andReturn();
        checkEmptyStatistics();

    }

    private void checkEmptyStatistics() throws Exception {
        this.mvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.count", is(0)))
                .andExpect(jsonPath("$.max", is("0.00")))
                .andExpect(jsonPath("$.min", is("0.00")))
                .andExpect(jsonPath("$.avg", is("0.00")))
                .andExpect(jsonPath("$.sum", is("0.00")))
                .andExpect(status().isOk())
                .andReturn();
    }

    private String createTransaction(final String amount, final String timestamp) {
        JSONObject transaction = new JSONObject();
        try {
            transaction.put("amount", amount);
            transaction.put("timestamp", timestamp);
        } catch (JSONException e) {
        }
        return transaction.toString();
    }

    private String getTimestamp(final long offset) {
        return TIMESTAMP_FORMATTER.format(Instant.now().plusMillis(offset));
    }
}
