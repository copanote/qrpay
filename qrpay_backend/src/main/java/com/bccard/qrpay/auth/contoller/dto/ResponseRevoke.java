package com.bccard.qrpay.auth.contoller.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseRevoke {
    private int count;
}
