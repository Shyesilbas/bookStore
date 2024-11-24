package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IllegalStockUpdateException extends RuntimeException {
    public IllegalStockUpdateException(String s) {
        super(s);
    }
}
