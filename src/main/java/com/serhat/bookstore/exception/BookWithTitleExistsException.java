package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BookWithTitleExistsException extends RuntimeException {
    public BookWithTitleExistsException(String s) {
        super(s);
    }
}
