package com.serhat.bookstore.dto;

public record VerifyCustomerResponse(
        String message,
        String customerUsername,
        String phone
) {
}
