package com.serhat.bookstore.dto;

public record LikeResponse(
        String message,
        String customerName,
        String comment
) {
}
