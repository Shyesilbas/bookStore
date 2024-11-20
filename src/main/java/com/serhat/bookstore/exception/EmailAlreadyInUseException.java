package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String m) {
        super(m);
    }
}
