package com.n26.services.impl;

import com.n26.cache.StatisticsCache;
import com.n26.models.Transaction;
import com.n26.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Transaction service implementation.
 * The service holds a shared cache that stores the
 * statistics of past transactions.
 */
@Component
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    @Qualifier("statisticsMapCache")
    private StatisticsCache statisticsCache;

    @Override
    public void addWithSyncronization(Transaction transaction) {
        statisticsCache.addWithSynchronization(transaction);
    }
    @Override
    public void addWithoutSyncronization(Transaction transaction) {
        statisticsCache.addWithoutSynchronization(transaction);
    }

    @Override
    public void delete() {
        statisticsCache.clear();
    }


}
