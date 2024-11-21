package com.serhat.bookstore.dto;

import com.serhat.bookstore.model.BookStatus;
import com.serhat.bookstore.model.Genre;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AddBookRequest(
         String isbn,
         String title,
         String author,
         Genre genre,
         LocalDate releaseDate,
         double rate,
         BookStatus bookStatus,
         int quantity,
         BigDecimal price
) {
}
