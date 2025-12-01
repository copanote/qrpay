package com.bccard.qrpay.domain.transaction.converter;

import com.bccard.qrpay.domain.common.code.PaymentStatus;
import com.bccard.qrpay.domain.common.converter.DatabaseCodeConverter;
import jakarta.persistence.Converter;

@Converter
public class PaymentStatusConverter extends DatabaseCodeConverter<PaymentStatus> {
    protected PaymentStatusConverter() {
        super(PaymentStatus.class);
    }
}
