package com.bccard.qrpay.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseRevoke {
    private int count;
}
