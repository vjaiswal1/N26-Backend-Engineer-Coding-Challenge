package com.n26.exceptions;

/**
 * Exception for invalid transactions
 */
public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(final String message) {
        super(message);
    }
}
