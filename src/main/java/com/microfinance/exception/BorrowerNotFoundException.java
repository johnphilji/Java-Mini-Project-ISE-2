package com.microfinance.exception;

/**
 * Exception thrown when a borrower is not found in the database
 */
public class BorrowerNotFoundException extends Exception {
    public BorrowerNotFoundException(String message) {
        super(message);
    }

    public BorrowerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
