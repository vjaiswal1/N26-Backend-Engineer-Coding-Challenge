package com.n26.validators;

import com.n26.exceptions.ExpiredTransactionException;
import com.n26.exceptions.FutureTransactionException;
import com.n26.exceptions.InvalidTransactionException;
import com.n26.models.Statistics;
import com.n26.models.Transaction;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * A validator class that validates transactions and statistics
 */
@Component
@Setter
public class Validator {

    @Value("${aggregator.window.size}")
    private int aggregatorWindowSize;

    public void validateTransaction(final Transaction transaction) {
        if (Objects.isNull(transaction.getAmount()) || Objects.isNull(transaction.getTimestamp())) {
            throw new InvalidTransactionException("Transaction is Invalid");
        }
        final long currentTime = System.currentTimeMillis();
        if (currentTime - transaction.getTimestamp() < 0) {
            throw new FutureTransactionException("Transaction happens in the future");
        } else if (currentTime - transaction.getTimestamp() > aggregatorWindowSize) {
            throw new ExpiredTransactionException("Transaction expired - Not in aggregator window");
        }
    }

    public boolean isValidStatistics(final Statistics statistics) {
        final long currentTime = System.currentTimeMillis();
        return (currentTime - statistics.getTimestamp() <= aggregatorWindowSize);
    }
}
