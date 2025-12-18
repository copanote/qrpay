package com.bccard.qrpay.controller.api.dtos;

import com.bccard.qrpay.domain.common.code.QrKitShippingStatus;
import com.bccard.qrpay.domain.qrkit.MpmQrKitApplication;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QrKitApplicationDto {
    private String merchantId;
    private String applicationAt;
    private QrKitShippingStatus shippingStatus;
    private String registeredMailNo;

    public static QrKitApplicationDto from(MpmQrKitApplication mpmQrKitApplication) {
        return QrKitApplicationDto.builder()
                .merchantId(mpmQrKitApplication.getMerchantId())
                .applicationAt(mpmQrKitApplication.getCreatedAt())
                .shippingStatus(mpmQrKitApplication.getStatus())
                .registeredMailNo(mpmQrKitApplication.getRegisteredMailNo())
                .build();
    }
}
