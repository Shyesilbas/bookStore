package com.serhat.bookstore.dto;

public record CustomerResponse(
        String message,
        Long customerId,
        String username
) {
}
