package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsernameAlreadyInUseException extends RuntimeException {
    public UsernameAlreadyInUseException(String m) {
        super(m);
    }
}
