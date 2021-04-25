package com.n26.services.impl;

import com.n26.cache.StatisticsCache;
import com.n26.models.Statistics;
import com.n26.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Statistics service implementation.
 * Service holds a shared cache that stores the
 * statistics of past transactions.
 */
@Component
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    @Qualifier("statisticsMapCache")
    private StatisticsCache statisticsCache;

    @Override
    public Statistics get() {
        return statisticsCache.getStatistics();
    }
}
