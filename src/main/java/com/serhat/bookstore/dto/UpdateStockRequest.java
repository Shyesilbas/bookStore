package com.serhat.bookstore.dto;

public record UpdateStockRequest(
        String isbn,
        int newQuantity
) {
}
