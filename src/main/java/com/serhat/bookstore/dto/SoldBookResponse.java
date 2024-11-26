package com.serhat.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SoldBookResponse(
        String isbn,
        String customerName,
        String title,
        Long processId,
        LocalDateTime date,
        BigDecimal fee
) {
}
