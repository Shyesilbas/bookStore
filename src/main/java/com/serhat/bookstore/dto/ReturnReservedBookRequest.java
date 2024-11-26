package com.serhat.bookstore.dto;

public record ReturnReservedBookRequest(
        Long reservationId,
        String isbn
) {
}
