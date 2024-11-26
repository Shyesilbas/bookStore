package com.serhat.bookstore.exception;

public class NoExpiredReservationsException extends RuntimeException {
    public NoExpiredReservationsException(String noActiveReservationFound) {
        super(noActiveReservationFound);
    }
}
