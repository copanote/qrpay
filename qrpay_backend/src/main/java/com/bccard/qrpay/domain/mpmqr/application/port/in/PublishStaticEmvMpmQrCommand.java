package com.bccard.qrpay.domain.mpmqr.application.port.in;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import lombok.Builder;

@Builder
public record PublishStaticEmvMpmQrCommand(
        String memberId,
        String merchantId,
        PointOfInitMethod pim,
        String currency,
        String startedAt,
        String affiliateId,
        String affiliateRequestValue) {

    public static PublishStaticEmvMpmQrCommand staticEmvMpm(String memberId, String merchantId, String currency) {

        return PublishStaticEmvMpmQrCommand.builder()
                .pim(PointOfInitMethod.STATIC)
                .memberId(memberId)
                .merchantId(merchantId)
                .currency(currency)
                .build();
    }

    public static PublishStaticEmvMpmQrCommand staticEmvMpm(String memberId, String merchantId) {
        return staticEmvMpm(memberId, merchantId, "410");
    }
}
