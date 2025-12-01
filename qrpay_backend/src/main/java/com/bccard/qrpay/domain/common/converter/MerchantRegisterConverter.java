package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.MerchantRegister;
import jakarta.persistence.Converter;

@Converter
public class MerchantRegisterConverter extends DatabaseCodeConverter<MerchantRegister> {
    protected MerchantRegisterConverter() {
        super(MerchantRegister.class);
    }
}
