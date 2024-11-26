package com.serhat.bookstore.exception;

public class BookOutOfStocksException extends RuntimeException {
    public BookOutOfStocksException(String s) {
        super(s);
    }
}
