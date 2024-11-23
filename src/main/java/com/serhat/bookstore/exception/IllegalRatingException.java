package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IllegalRatingException extends RuntimeException {
    public IllegalRatingException(String s) {
        super(s);
    }
}
