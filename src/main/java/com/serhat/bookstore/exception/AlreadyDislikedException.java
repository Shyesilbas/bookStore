package com.serhat.bookstore.exception;

public class AlreadyDislikedException extends RuntimeException {
    public AlreadyDislikedException(String s) {
        super(s);
    }
}
