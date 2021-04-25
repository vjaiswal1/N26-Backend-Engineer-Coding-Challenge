package com.n26.exceptions;

/**
 * Exception for future transactions
 */
public class FutureTransactionException extends RuntimeException {
    public FutureTransactionException(String message) {
        super(message);
    }
}
