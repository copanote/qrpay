package com.bccard.qrpay.auth.contoller.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RequestRefresh {
    private String refreshToken;
}
