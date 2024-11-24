package com.serhat.bookstore.dto;

public record UpdateStockResponse(
        String message,
        String isbn,
        int newQuantity
) {
}
