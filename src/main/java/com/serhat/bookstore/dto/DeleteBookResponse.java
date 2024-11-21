package com.serhat.bookstore.dto;

public record DeleteBookResponse(
        String message,
        String isbn,
        String title
) {
}
