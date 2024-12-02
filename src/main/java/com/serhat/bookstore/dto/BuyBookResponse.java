package com.serhat.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BuyBookResponse(
        String message,
        String customerUsername,
        String title,
        BigDecimal fee,
        LocalDateTime date,
        BigDecimal youPayed,
        BigDecimal youSaved,
        String saleRate
) {
}
