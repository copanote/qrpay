package com.bccard.qrpay.domain.mpmqr.dto;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.merchant.Merchant;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class PublishBcEmvMpmQrReqDto {
    private String memberId;
    private Merchant merchant;
    private PointOfInitMethod pim;
    private Long amount;
    private Long installment;
    private String currency;
    private String startedAt;
    private String affiliateId;
    private String affiliateRequestValue;

    public static PublishBcEmvMpmQrReqDto dynamicEmvMpm(String memberId, Merchant merchant, Long amount, Long installment, String currency) {
        return PublishBcEmvMpmQrReqDto
                .builder()
                .pim(PointOfInitMethod.DYNAMIC)
                .memberId(memberId)
                .merchant(merchant)
                .amount(amount)
                .installment(installment)
                .currency(currency)
                .build();
    }

    public static PublishBcEmvMpmQrReqDto staticEmvMpm(String memberId, Merchant merchant, String currency) {
        return PublishBcEmvMpmQrReqDto
                .builder()
                .pim(PointOfInitMethod.STATIC)
                .memberId(memberId)
                .merchant(merchant)
                .currency(currency)
                .build();
    }


}
