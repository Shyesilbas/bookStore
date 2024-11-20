package com.serhat.bookstore.dto;

public record CustomerRequest(
        String username,
        String password,
        String email,
        String phone
) {
}
