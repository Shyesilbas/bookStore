package com.serhat.bookstore.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReserveBookRequest(
        String isbn,
        LocalDate reservationUntil
) {
}
