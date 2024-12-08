package com.serhat.bookstore.dto;

import com.serhat.bookstore.model.Genre;

import java.math.BigDecimal;

public record MostSellers(
        String isbn,
        String author,
        String title,
        Genre genre,
        BigDecimal fee
) {
}
