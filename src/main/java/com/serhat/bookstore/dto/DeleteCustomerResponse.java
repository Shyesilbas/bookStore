package com.serhat.bookstore.dto;

public record DeleteCustomerResponse(
        Long customerId,
        String name,
        String email
) {
}
