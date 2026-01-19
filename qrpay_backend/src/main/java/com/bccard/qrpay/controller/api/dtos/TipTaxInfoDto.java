package com.bccard.qrpay.controller.api.dtos;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipTaxInfoDto {
    private BigDecimal vatRate;
    private BigDecimal tipRate;
}
