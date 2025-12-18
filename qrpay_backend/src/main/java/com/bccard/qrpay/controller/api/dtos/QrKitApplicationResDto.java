package com.bccard.qrpay.controller.api.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QrKitApplicationResDto {
    private List<QrKitApplicationDto> applications;

    public static QrKitApplicationResDto of(List<QrKitApplicationDto> list) {
        return QrKitApplicationResDto.builder()
                .applications(list)
                .build();
    }
}
