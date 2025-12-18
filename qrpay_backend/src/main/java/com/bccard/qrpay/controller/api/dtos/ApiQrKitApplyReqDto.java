package com.bccard.qrpay.controller.api.dtos;

import lombok.Getter;

@Getter
public class ApiQrKitApplyReqDto {
    private String merchantId;
    private String merchantName;
    private String address1;
    private String address2;
    private String zip;
    private String phoneNo;
}
