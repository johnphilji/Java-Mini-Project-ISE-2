package com.microfinance.exception;

/**
 * Exception thrown when an invalid loan amount is provided
 */
public class InvalidLoanAmountException extends Exception {
    public InvalidLoanAmountException(String message) {
        super(message);
    }

    public InvalidLoanAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
