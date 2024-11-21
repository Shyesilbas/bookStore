package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PhoneAlreadyInUseException extends RuntimeException {
    public PhoneAlreadyInUseException(String s) {
        super(s);
    }
}
