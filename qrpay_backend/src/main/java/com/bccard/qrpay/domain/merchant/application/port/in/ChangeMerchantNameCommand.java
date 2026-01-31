package com.bccard.qrpay.domain.merchant.application.port.in;

import lombok.Builder;

@Builder
public record ChangeMerchantNameCommand(String merchantId, String toUpdateName) {}
