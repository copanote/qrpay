package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.QrKitShippingStatus;
import jakarta.persistence.Converter;

@Converter
public class QrKitShippingStatusConverter extends DatabaseCodeConverter<QrKitShippingStatus> {
    protected QrKitShippingStatusConverter() {
        super(QrKitShippingStatus.class);
    }
}
