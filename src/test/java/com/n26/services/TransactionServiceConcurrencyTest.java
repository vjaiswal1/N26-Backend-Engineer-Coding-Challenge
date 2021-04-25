package com.n26.services;

import com.n26.models.Statistics;
import com.n26.models.Transaction;
import com.n26.services.impl.StatisticsServiceImpl;
import com.n26.services.impl.TransactionServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TransactionServiceConcurrencyTest {

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @Autowired
    private StatisticsServiceImpl statisticsServiceImpl;

    private static Integer CONCURRENCY_COUNTER=1000;



    @Test
    public void addTransactionWithSyncronization() throws ExecutionException, InterruptedException {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ONE);
        transaction.setTimestamp(Instant.now().toEpochMilli());



        ExecutorService service = Executors.newFixedThreadPool(10);

        IntStream.range(0, CONCURRENCY_COUNTER).parallel()
                .forEach(count -> {
                    service.submit(()->transactionServiceImpl.addWithSyncronization(transaction));
                });

        service.awaitTermination(10, TimeUnit.SECONDS);
        Future<Statistics> averageResult =
                service.submit(() -> statisticsServiceImpl.get());

        assertEquals(averageResult.get().sum, BigDecimal.valueOf(CONCURRENCY_COUNTER));

    }

    @Test
    public void addTransactionWithoutSyncronization() throws ExecutionException, InterruptedException {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ONE);
        transaction.setTimestamp(Instant.now().toEpochMilli());



        ExecutorService service = Executors.newFixedThreadPool(10);

        IntStream.range(0, CONCURRENCY_COUNTER).parallel()
                .forEach(count -> {
                    service.submit(()->transactionServiceImpl.addWithoutSyncronization(transaction));
                });

        service.awaitTermination(10, TimeUnit.SECONDS);
        Future<Statistics> averageResult =
                service.submit(() -> statisticsServiceImpl.get());

        assertNotEquals(averageResult.get().sum, BigDecimal.valueOf(CONCURRENCY_COUNTER));

    }



}