package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BookNotFoundForAuthorException extends RuntimeException {
    public BookNotFoundForAuthorException(String s) {
        super(s);
    }
}
