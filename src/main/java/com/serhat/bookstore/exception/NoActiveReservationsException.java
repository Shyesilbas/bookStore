package com.serhat.bookstore.exception;

public class NoActiveReservationsException extends RuntimeException {
    public NoActiveReservationsException(String s) {
        super(s);
    }
}
