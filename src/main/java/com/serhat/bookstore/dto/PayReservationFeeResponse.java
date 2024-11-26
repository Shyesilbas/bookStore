package com.serhat.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PayReservationFeeResponse(
        String message,
        String customerUsername,
        Long reservationId,
        BigDecimal fee,
        LocalDateTime paymentDay
) {
}
