package com.bccard.qrpay.controller.api.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BcMerchantInfo {
    private String merchantName;
    private String representativeName;
    private String merchantNo;
    private String telNo;
    private String zipCode;
    private String address;
    private String registeredAt; // yymmdd
}
