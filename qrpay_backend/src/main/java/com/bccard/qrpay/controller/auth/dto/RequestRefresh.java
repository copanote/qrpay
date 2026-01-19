package com.bccard.qrpay.controller.auth.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RequestRefresh {
    private String refreshToken;
}
