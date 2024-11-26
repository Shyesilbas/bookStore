package com.serhat.bookstore.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BookIsNotAvailableForReservationException extends RuntimeException {
    public BookIsNotAvailableForReservationException(String s) {
        super(s);
    }
}
