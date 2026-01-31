package com.bccard.qrpay.controller.api.dtos;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TipChangeReqDto {
    private boolean enableTip;
    private Long tipRate;
}
