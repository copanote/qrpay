package com.bccard.qrpay.external.nice.dto;

import lombok.Getter;

@Getter
public class NiceSmsReqDto {
    private String referenceId;
    private String createdAt;
    private String smsVerificationRequestorId;
}
