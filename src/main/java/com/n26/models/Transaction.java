package com.n26.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.n26.utils.deserializers.AmountDeserializer;
import com.n26.utils.deserializers.TimestampDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Transaction class that holds a transaction details
 */
@Getter
@Setter
@ToString
public class Transaction {
    @JsonProperty
    @JsonDeserialize(using = AmountDeserializer.class)
    private BigDecimal amount;

    @JsonProperty
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Long timestamp;

}
