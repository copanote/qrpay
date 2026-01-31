package com.bccard.qrpay.domain.mpmqr.application.port.in;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import lombok.Builder;

@Builder
public record PublishDynamicEmvMpmQrCommand(
        String memberId,
        String merchantId,
        PointOfInitMethod pim,
        Long amount,
        Long installment,
        String currency,
        String startedAt,
        String affiliateId,
        String affiliateRequestValue) {

    public static PublishDynamicEmvMpmQrCommand staticEmvMpm(String memberId, String merchantId, String currency) {

        return PublishDynamicEmvMpmQrCommand.builder()
                .pim(PointOfInitMethod.STATIC)
                .memberId(memberId)
                .merchantId(merchantId)
                .currency(currency)
                .build();
    }

    public static PublishDynamicEmvMpmQrCommand staticEmvMpm(String memberId, String merchantId) {
        return staticEmvMpm(memberId, merchantId, "410");
    }
}
