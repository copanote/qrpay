package com.bccard.qrpay.domain.common.converter;

import jakarta.persistence.AttributeConverter;
import java.util.Currency;

public class CurrencyToNumericConverter implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency currency) {
        return "";
    }

    @Override
    public Currency convertToEntityAttribute(String s) {
        return null;
    }
}
