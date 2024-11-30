package com.serhat.bookstore.dto;

public record leastRatedCommentsForBookResponse(
        String username,
        String comment,
        int likes,
        int dislikes,
        int reposts,
        double rate
) {
}
