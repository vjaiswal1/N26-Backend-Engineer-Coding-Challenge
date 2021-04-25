package com.n26.exceptions;

/**
 * Exception for expired transactions
 */
public class ExpiredTransactionException extends RuntimeException {
    public ExpiredTransactionException(final String message) {
        super(message);
    }
}
