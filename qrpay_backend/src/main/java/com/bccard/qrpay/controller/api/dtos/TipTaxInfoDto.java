package com.bccard.qrpay.controller.api.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipTaxInfoDto {
    private Long vatRate;
    private Long tipRate;
}
