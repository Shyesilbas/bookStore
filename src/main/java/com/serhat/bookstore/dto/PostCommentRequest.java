package com.serhat.bookstore.dto;

public record PostCommentRequest(
        String isbn,
        double rate,
        String comment
) {
}
