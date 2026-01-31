package com.bccard.qrpay.domain.merchant.application.port.in;

import lombok.Builder;

@Builder
public record TipUpdateCommand(
        String merchantId,
        boolean enable,
        Long tipRate
) {
}
