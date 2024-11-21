package com.serhat.bookstore.dto;

import com.serhat.bookstore.model.Genre;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AddBookResponse(
        String message,
        String title,
        String author,
        String isbn,
        Genre genre,
        double rate,
        BigDecimal price

) {
}
