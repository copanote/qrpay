package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.AuthorizeType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AuthorizedConverter implements AttributeConverter<AuthorizeType, String> {

    @Override
    public String convertToDatabaseColumn(AuthorizeType attribute) {
        return attribute.getDbCode();
    }

    @Override
    public AuthorizeType convertToEntityAttribute(String dbData) {
        return AuthorizeType.of(dbData);
    }
}
