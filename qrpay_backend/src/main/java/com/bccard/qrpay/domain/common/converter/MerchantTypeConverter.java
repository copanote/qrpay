package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.MerchantType;
import jakarta.persistence.Converter;

@Converter
public class MerchantTypeConverter extends DatabaseCodeConverter<MerchantType> {
    protected MerchantTypeConverter() {
        super(MerchantType.class);
    }
}
