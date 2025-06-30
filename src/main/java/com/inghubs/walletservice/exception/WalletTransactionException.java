package com.inghubs.walletservice.exception;

public class WalletTransactionException extends RuntimeException {

    public WalletTransactionException(String message) {
        super(message);
    }

    public WalletTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

