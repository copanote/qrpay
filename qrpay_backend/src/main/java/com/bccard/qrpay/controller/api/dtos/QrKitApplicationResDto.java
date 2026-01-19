package com.bccard.qrpay.controller.api.dtos;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QrKitApplicationResDto {
    private List<QrKitApplicationDto> applications;

    public static QrKitApplicationResDto of(List<QrKitApplicationDto> list) {
        return QrKitApplicationResDto.builder().applications(list).build();
    }
}
