package com.serhat.bookstore.dto;

public record ReturnReservedBookResponse(
        String message,
        String customerName,
        String isbn,
        Long reservationId,
        String title
) {
}
