package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountCannotBeDeletedException extends RuntimeException {
    public AccountCannotBeDeletedException(String s) {
        super(s);
    }
}
