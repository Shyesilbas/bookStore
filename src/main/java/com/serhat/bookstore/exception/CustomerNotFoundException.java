package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
