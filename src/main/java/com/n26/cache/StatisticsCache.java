package com.n26.cache;

import com.n26.models.Statistics;
import com.n26.models.Transaction;

/**
 * Interface for cache holding transaction statistics
 */
public interface StatisticsCache {

   public void addWithSynchronization(Transaction transaction);
    public void addWithoutSynchronization(Transaction transaction);


    void clear();

    Statistics getStatistics();
}
