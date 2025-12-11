package com.bccard.qrpay.controller.api.dtos;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class MpmQrInfoResDto {
    private PointOfInitMethod pim;
    private Long amount;
    private Long installment;
    private String qrBase64Image;
    private String merchantName;


    public static MpmQrInfoResDto staticMpmQrInfo(String merchantName, String qrBase64Image) {
        return MpmQrInfoResDto.builder()
                .pim(PointOfInitMethod.STATIC)
                .merchantName(merchantName)
                .qrBase64Image(qrBase64Image)
                .build();
    }

    public static MpmQrInfoResDto dynamicMpmQrInfo(String merchantName, String qrBase64Image, Long amount, Long installment) {
        return MpmQrInfoResDto.builder()
                .pim(PointOfInitMethod.DYNAMIC)
                .merchantName(merchantName)
                .qrBase64Image(qrBase64Image)
                .amount(amount)
                .installment(installment)
                .build();
    }

}
