package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.ApprovedType;
import jakarta.persistence.Converter;

@Converter
public class ApprovedTypeConverter extends DatabaseCodeConverter<ApprovedType> {
    protected ApprovedTypeConverter() {
        super(ApprovedType.class);
    }
}
