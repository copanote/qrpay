package com.bccard.qrpay.domain.merchant.application.port.in;

import lombok.Builder;

@Builder
public record MerchantNameChangeCommand(String merchantId, String toUpdateName) {
}
