package com.serhat.bookstore.exception;

public class UsernameMismatchException extends RuntimeException {
    public UsernameMismatchException(String s) {
        super(s);
    }
}
