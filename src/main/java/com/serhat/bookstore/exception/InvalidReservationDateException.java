package com.serhat.bookstore.exception;

public class InvalidReservationDateException extends RuntimeException {
    public InvalidReservationDateException(String s) {
        super(s);
    }
}
