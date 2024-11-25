package com.serhat.bookstore.dto;

import java.math.BigDecimal;

public record UpdateBookPriceResponse(
        String message,
        String isbn,
        String title,
        BigDecimal newPrice
) {
}
