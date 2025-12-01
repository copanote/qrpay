package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.MerchantStatus;
import jakarta.persistence.Converter;

@Converter
public class MerchantStatusConverter extends DatabaseCodeConverter<MerchantStatus> {
    protected MerchantStatusConverter() {
        super(MerchantStatus.class);
    }
}
