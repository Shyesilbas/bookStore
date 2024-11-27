package com.serhat.bookstore.exception;

public class CustomerAlreadyVerifiedException extends RuntimeException {
    public CustomerAlreadyVerifiedException(String s) {
        super(s);
    }
}
