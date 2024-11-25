package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IllegalPriceUpdateException extends RuntimeException {
    public IllegalPriceUpdateException(String s) {
        super(s);
    }
}
