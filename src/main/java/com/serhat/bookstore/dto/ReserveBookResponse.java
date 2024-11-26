package com.serhat.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReserveBookResponse(
        String message,
        String title,
        String isbn,
        String customerUsername,
        LocalDateTime by,
        LocalDateTime until,
        BigDecimal reservationFee
) {
}
