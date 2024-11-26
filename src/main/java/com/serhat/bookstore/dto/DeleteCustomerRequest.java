package com.serhat.bookstore.dto;

public record DeleteCustomerRequest(
        String username,
        String password
) {
}
