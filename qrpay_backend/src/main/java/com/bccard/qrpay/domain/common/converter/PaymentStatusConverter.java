package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.PaymentStatus;
import jakarta.persistence.Converter;

@Converter
public class PaymentStatusConverter extends DatabaseCodeConverter<PaymentStatus> {
    protected PaymentStatusConverter() {
        super(PaymentStatus.class);
    }
}
