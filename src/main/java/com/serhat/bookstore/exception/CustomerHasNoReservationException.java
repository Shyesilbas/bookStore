package com.serhat.bookstore.exception;

public class CustomerHasNoReservationException extends RuntimeException {
    public CustomerHasNoReservationException(String s) {
        super(s);
    }
}
