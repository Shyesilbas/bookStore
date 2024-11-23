package com.serhat.bookstore.dto;

import com.serhat.bookstore.model.Genre;

import java.math.BigDecimal;

public record BookResponse(
        String title,
        String author,
        String isbn,
        Genre genre,
        double rate,
        BigDecimal price
) {
}
