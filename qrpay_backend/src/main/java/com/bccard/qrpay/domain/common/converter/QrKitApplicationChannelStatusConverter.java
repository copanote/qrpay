package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.QrKitApplicationChannel;
import jakarta.persistence.Converter;

@Converter
public class QrKitApplicationChannelStatusConverter extends DatabaseCodeConverter<QrKitApplicationChannel> {
    protected QrKitApplicationChannelStatusConverter() {
        super(QrKitApplicationChannel.class);
    }
}
