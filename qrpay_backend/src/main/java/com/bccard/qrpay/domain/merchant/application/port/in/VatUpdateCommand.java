package com.bccard.qrpay.domain.merchant.application.port.in;

import lombok.Builder;

@Builder
public record VatUpdateCommand(
        String merchantId,
        boolean enable,
        Long vatRate
) {
}
