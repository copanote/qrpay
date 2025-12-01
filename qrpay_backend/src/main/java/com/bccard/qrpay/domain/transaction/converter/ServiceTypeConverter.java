package com.bccard.qrpay.domain.transaction.converter;

import com.bccard.qrpay.domain.common.code.ServiceType;
import com.bccard.qrpay.domain.common.converter.DatabaseCodeConverter;
import jakarta.persistence.Converter;

@Converter
public class ServiceTypeConverter extends DatabaseCodeConverter<ServiceType> {
    protected ServiceTypeConverter() {
        super(ServiceType.class);
    }
}
