package com.serhat.bookstore.dto;

import com.serhat.bookstore.model.Genre;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SoldBookResponse(
        String isbn,
        String customerName,
        String title,
        Genre genre,
        Long processId,
        LocalDateTime date,
        BigDecimal fee
) {
}
