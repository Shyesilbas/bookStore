package com.serhat.bookstore.exception;

public class NoBooksSoldException extends RuntimeException {
    public NoBooksSoldException(String s) {
        super(s);
    }
}
