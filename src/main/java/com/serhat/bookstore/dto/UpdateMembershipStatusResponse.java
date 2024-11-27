package com.serhat.bookstore.dto;

import com.serhat.bookstore.model.MemberShipStatus;

import java.math.BigDecimal;

public record UpdateMembershipStatusResponse(
        String customerUsername,
        MemberShipStatus oldMemberShip,
        MemberShipStatus updatedMemberShip,
        BigDecimal price
) {
}
