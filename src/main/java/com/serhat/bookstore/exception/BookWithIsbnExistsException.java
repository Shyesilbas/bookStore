package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BookWithIsbnExistsException extends RuntimeException {
    public BookWithIsbnExistsException(String s) {
        super(s);
    }
}
