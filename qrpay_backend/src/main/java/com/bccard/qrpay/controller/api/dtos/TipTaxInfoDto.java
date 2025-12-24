package com.bccard.qrpay.controller.api.dtos;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TipTaxInfoDto {
    private BigDecimal vatRate;
    private BigDecimal tipRate;
}
