package com.serhat.bookstore.exception;

public class ActiveReservationLimitReachedException extends RuntimeException {
    public ActiveReservationLimitReachedException(String s) {
        super(s);
    }
}
