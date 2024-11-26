package com.serhat.bookstore.exception;

public class ReservationFeeNotPayedException extends RuntimeException {
    public ReservationFeeNotPayedException(String s) {
        super(s);
    }
}
