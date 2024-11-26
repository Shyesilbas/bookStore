package com.serhat.bookstore.exception;

public class BookAlreadyReservedByThisCustomerException extends RuntimeException {
    public BookAlreadyReservedByThisCustomerException(String s) {
        super(s);
    }
}
