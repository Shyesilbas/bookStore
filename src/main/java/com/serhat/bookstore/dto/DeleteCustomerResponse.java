package com.serhat.bookstore.dto;

public record DeleteCustomerResponse(
        String message,
        Long customerId,
        String name,
        String email
) {
}
