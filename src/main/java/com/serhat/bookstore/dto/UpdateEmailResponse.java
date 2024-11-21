package com.serhat.bookstore.dto;

public record UpdateEmailResponse(
        String message,
        String username,
        String updatedEmail
) {
}
