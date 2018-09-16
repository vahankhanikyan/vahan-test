package com.revolut.vahan.test.exception;

public class InsufficientAmountException extends Exception {
    public InsufficientAmountException(int senderId, double amountToSend, double amountAvailable) {
        this(String.format("Sender (UserID: %d) haven't enough money to sent. Requested: %.2f, available: %.2f", senderId, amountToSend, amountAvailable));
    }

    public InsufficientAmountException(String message) {
        super(message);
    }

    public InsufficientAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientAmountException(Throwable cause) {
        super(cause);
    }

    public InsufficientAmountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
