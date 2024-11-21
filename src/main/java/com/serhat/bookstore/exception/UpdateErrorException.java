package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateErrorException extends RuntimeException {
    public UpdateErrorException(String s) {
        super(s);
    }
}
