package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BlankEntryException extends RuntimeException {
    public BlankEntryException(String s) {
        super(s);
    }
}
