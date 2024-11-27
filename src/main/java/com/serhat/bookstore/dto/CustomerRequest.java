package com.serhat.bookstore.dto;

import com.serhat.bookstore.model.MemberShipStatus;

public record CustomerRequest(
        String username,
        String password,
        MemberShipStatus memberShipStatus,
        String email,
        String phone
) {
}
