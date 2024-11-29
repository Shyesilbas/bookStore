package com.serhat.bookstore.dto;

public record DislikeResponse(
        String message,
        String customerName,
        String comment
) {
}
