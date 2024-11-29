package com.serhat.bookstore.dto;

public record CommentResponse(
        String username,
        Long commentId,
        String title,
        String comment,
        int likes,
        int dislikes,
        int reposts
) {
}
