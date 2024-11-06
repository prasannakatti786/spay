package com.spay.wallet.exections;

public class TransactionException extends RuntimeException {

    public TransactionException(String message) {
        super(message);
    }
}
