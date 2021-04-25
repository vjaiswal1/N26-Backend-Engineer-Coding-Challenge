package com.n26.cache.impl;

import com.n26.cache.StatisticsCache;
import com.n26.models.Statistics;
import com.n26.models.Transaction;
import com.n26.validators.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Cache implementation using ConcurrentHashMap
 */
@Component("statisticsMapCache")
public class StatisticsMapCache implements StatisticsCache {

    @Value("${aggregator.window.size}")
    private int aggregatorWindowSize;

    @Value("${aggregator.bucket.size}")
    private int aggregatorBucketSize;

    @Value("${statistics.decimal.scale}")
    private int decimalScale;

    @Autowired
    private Validator validator;

    private Map<Integer, Statistics> concurrentHashMap;

    private int numberOfBuckets;

    @PostConstruct
    private void init() {
        numberOfBuckets = aggregatorWindowSize / aggregatorBucketSize;
        concurrentHashMap = new ConcurrentHashMap<>(numberOfBuckets);
        for (int i = 0; i < numberOfBuckets; i++) {
            concurrentHashMap.put(i, new Statistics());
        }
    }

    @Override
    public void addWithSynchronization(Transaction transaction) {
        validator.validateTransaction(transaction);
        final int bucketIndex = getBucket(transaction);
         Statistics statistics = concurrentHashMap.get(bucketIndex);
        synchronized (statistics) {

            if (validator.isValidStatistics(statistics)) {
                merge(statistics, transaction);
            } else {
                statistics.clear();
                init(statistics, transaction);
            }
        }
    }

    @Override
    public void addWithoutSynchronization(Transaction transaction) {
        validator.validateTransaction(transaction);
        final int bucketIndex = getBucket(transaction);
        Statistics statistics = concurrentHashMap.get(bucketIndex);

            if (validator.isValidStatistics(statistics)) {
                merge(statistics, transaction);
            } else {
                statistics.clear();
                init(statistics, transaction);
            }

    }
    @Override
    public void clear() {
        concurrentHashMap.clear();
        init();
    }

    @Override
    public Statistics getStatistics() {
        final List<Statistics> statisticsList = concurrentHashMap.values().stream()
                .filter(statistics -> {
                    synchronized (statistics) {
                        return validator.isValidStatistics(statistics);
                    }
                })
                .collect(Collectors.toList());
        final Statistics aggregatedStatistics = new Statistics();
        statisticsList.forEach(statistics -> {
            synchronized (statistics) {
                aggregate(aggregatedStatistics, statistics);
            }
        });
        aggregatedStatistics.check();
        return aggregatedStatistics;
    }

    private int getBucket(final Transaction transaction) {
        final long currentTime = System.currentTimeMillis();
        final int bucketIndex = (int) (((currentTime - transaction.getTimestamp()) / aggregatorBucketSize));
        return bucketIndex % numberOfBuckets;
    }

    private void merge(final Statistics statistics, final Transaction transaction) {
        statistics.setSum(statistics.getSum().add(transaction.getAmount()));
        statistics.setCount(statistics.getCount() + 1L);
        statistics.setAvg(statistics.getSum()
                .divide(BigDecimal.valueOf(statistics.getCount()), decimalScale, RoundingMode.HALF_UP));
        statistics.setMin(transaction.getAmount().min(statistics.getMin()));
        statistics.setMax(transaction.getAmount().max(statistics.getMax()));
    }

    public Statistics aggregate(final Statistics aggregatedStatistics, final Statistics statistics) {
        aggregatedStatistics.setSum(aggregatedStatistics.getSum().add(statistics.getSum()));
        aggregatedStatistics.setCount(aggregatedStatistics.getCount() + statistics.getCount());
        aggregatedStatistics.setAvg(aggregatedStatistics.getSum()
                .divide(BigDecimal.valueOf(aggregatedStatistics.getCount()), decimalScale, RoundingMode.HALF_UP));
        aggregatedStatistics.setMin(statistics.getMin().min(aggregatedStatistics.getMin()));
        aggregatedStatistics.setMax(statistics.getMax().max(aggregatedStatistics.getMax()));
        return aggregatedStatistics;
    }

    private Statistics init(final Statistics statistics, final Transaction transaction) {
        statistics.setMin(transaction.getAmount());
        statistics.setMax(transaction.getAmount());
        statistics.setCount(1L);
        statistics.setAvg(transaction.getAmount());
        statistics.setSum(transaction.getAmount());
        statistics.setTimestamp(transaction.getTimestamp());
        return statistics;
    }
}
