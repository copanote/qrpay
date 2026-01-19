package com.bccard.qrpay.controller.page.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class QrKitApplyPageDto {
    private String merchantId;
    private String merchantName;
    private String phoneNo;
    private String postNo;
    private String address1;
    private String address2;
}
