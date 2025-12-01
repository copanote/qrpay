package com.bccard.qrpay.controller.api.dtos;

import lombok.Data;

@Data
public class EmvMpmQrPublishInDto {
    private String reqDataType;
    private String reqDataValue;
    private String qrType;
    private String amount;
    private String transactionCurrency;
    private String affiCoId;
    private String affiCoReqVal;
    private String imageSize;
    private String imageFormat;
}
