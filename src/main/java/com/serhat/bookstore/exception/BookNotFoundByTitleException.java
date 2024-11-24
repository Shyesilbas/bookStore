package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BookNotFoundByTitleException extends RuntimeException {
    public BookNotFoundByTitleException(String s) {
        super(s);
    }
}
