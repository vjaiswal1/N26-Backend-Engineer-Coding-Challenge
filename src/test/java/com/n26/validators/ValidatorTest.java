package com.n26.validators;

import com.n26.exceptions.ExpiredTransactionException;
import com.n26.exceptions.FutureTransactionException;
import com.n26.exceptions.InvalidTransactionException;
import com.n26.models.Statistics;
import com.n26.models.Transaction;
import com.n26.validators.Validator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class ValidatorTest {
    private Validator validator;
    private static final int AGGREGATOR_WINDOW = 60000;

    @Before
    public void setUp() {
        this.validator = new Validator();
        this.validator.setAggregatorWindowSize(AGGREGATOR_WINDOW);
    }

    @Test(expected = InvalidTransactionException.class)
    public void testInvalidTransaction() {
        this.validator.validateTransaction(getStubbedTransaction(null, null));
    }

    @Test(expected = FutureTransactionException.class)
    public void testFutureTransaction() {
        this.validator.validateTransaction(getStubbedTransaction(BigDecimal.TEN,
                Instant.now().toEpochMilli() + AGGREGATOR_WINDOW));
    }

    @Test(expected = ExpiredTransactionException.class)
    public void testExpiredTransaction() {
        this.validator.validateTransaction(getStubbedTransaction(BigDecimal.TEN,
                Instant.now().toEpochMilli() - 2 * AGGREGATOR_WINDOW));
    }

    @Test
    public void testValidTransaction() {
        this.validator.validateTransaction(getStubbedTransaction(BigDecimal.TEN,
                Instant.now().toEpochMilli()));
    }


    @Test
    public void testInvalidStatistics() {
        assertFalse(this.validator.isValidStatistics(new Statistics()));
    }

    @Test
    public void testValidStatistics() {
        final Statistics statistics = new Statistics();
        statistics.setTimestamp(Instant.now().toEpochMilli());
        assertTrue(this.validator.isValidStatistics(statistics));
    }

    private Transaction getStubbedTransaction(final BigDecimal amount, final Long timestamp) {
        final Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTimestamp(timestamp);
        return transaction;
    }
}
