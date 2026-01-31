package com.bccard.qrpay.controller.api.dtos;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VatChangeReqDto {
    private boolean enableVat;
    private Long vatRate;
}
