package com.serhat.bookstore.exception;

public class AlreadyDoneException extends RuntimeException {
    public AlreadyDoneException(String s) {
        super(s);
    }
}
