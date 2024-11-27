package com.serhat.bookstore.dto;

import com.serhat.bookstore.model.MemberShipStatus;

public record UpdateMemberShipRequest(
        MemberShipStatus status
) {
}
