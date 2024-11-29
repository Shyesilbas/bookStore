package com.serhat.bookstore.dto;

public record PostCommentResponse(
        String message,
        String customerName,
        double rate,
        String comment
) {
}
