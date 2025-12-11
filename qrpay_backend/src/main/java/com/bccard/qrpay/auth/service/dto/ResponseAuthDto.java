package com.bccard.qrpay.auth.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ResponseAuthDto {
    private String memberId;
    private String accessToken;
    private String refreshToken;
}
