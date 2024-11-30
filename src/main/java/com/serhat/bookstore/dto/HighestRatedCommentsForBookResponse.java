package com.serhat.bookstore.dto;

public record HighestRatedCommentsForBookResponse(
        String username,
        String comment,
        int likes,
        int dislikes,
        int reposts,
        double rate
) {
}
