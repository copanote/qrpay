package com.bccard.qrpay.controller.api.dtos;

import lombok.Data;

@Data
public class EmvMpmQrPublishOutDto {
    private String code;
    private String description;
    private String qrBase64;
    private String regAton;
    private String qrReferencdId;
}
