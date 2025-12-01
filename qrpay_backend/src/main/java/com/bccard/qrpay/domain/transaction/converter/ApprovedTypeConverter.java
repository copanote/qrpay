package com.bccard.qrpay.domain.transaction.converter;

import com.bccard.qrpay.domain.common.code.ApprovedType;
import com.bccard.qrpay.domain.common.converter.DatabaseCodeConverter;
import jakarta.persistence.Converter;

@Converter
public class ApprovedTypeConverter extends DatabaseCodeConverter<ApprovedType> {
    protected ApprovedTypeConverter() {
        super(ApprovedType.class);
    }
}
