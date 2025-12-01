package com.bccard.qrpay.domain.common.converter;

import jakarta.persistence.AttributeConverter;

public class BooleanYnConverter implements AttributeConverter<Boolean, String> {


    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) {
            attribute = Boolean.FALSE;
        }


        if (attribute) {
            return "Y";
        } else {
            return "N";
        }
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if ("Y".equalsIgnoreCase(dbData)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }


}
