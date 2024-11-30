package com.serhat.bookstore.dto;

public record MostInteractedComments(
        String title,
        String comment,
        int likes,
        int dislikes,
        int reposts
) {
}
