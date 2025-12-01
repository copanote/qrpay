package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.QrTransactionType;
import jakarta.persistence.Converter;

@Converter
public class QrTransactionTypeConverter extends DatabaseCodeConverter<QrTransactionType> {
    protected QrTransactionTypeConverter() {
        super(QrTransactionType.class);
    }
}
