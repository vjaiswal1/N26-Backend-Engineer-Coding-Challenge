package com.n26.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.n26.utils.serializers.BigDecimalSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Statistics class that holds transaction statistics.
 * Using BigDecimal instead of double since transaction are amounts
 * and BigDecimal has better precision.
 */
@Getter
@Setter
@ToString
public class Statistics {
    private static final BigDecimal MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE);
    private static final BigDecimal MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);

    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal sum;

    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal avg;

    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal max;

    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal min;

    private Long count;

    @JsonIgnore
    private Long timestamp;

    public Statistics() {
        clear();
    }

    public void clear() {
        sum = BigDecimal.ZERO;
        avg = BigDecimal.ZERO;
        max = MIN_VALUE;
        min = MAX_VALUE;
        count = 0L;
        timestamp = 0L;
    }

    /**
     * Method that checks the object for default values.
     * This has to be called before the statistics is
     * consumed for external use.
     */
    public void check() {
        if (max.equals(MIN_VALUE)) {
            max = BigDecimal.ZERO;
        }
        if (min.equals(MAX_VALUE)) {
            min = BigDecimal.ZERO;
        }
    }
}