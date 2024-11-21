package com.serhat.bookstore.dto;

public record UpdatePhoneNumberResponse(
        String message,
        String username,
        String newPhone
) {
}
