package com.serhat.bookstore.dto;

import java.math.BigDecimal;

public record UpdatePriceRequest(
        String isbn,
        BigDecimal newPrice
) {
}
