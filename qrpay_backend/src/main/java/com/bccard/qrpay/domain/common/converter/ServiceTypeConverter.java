package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.ServiceType;

public class ServiceTypeConverter extends DatabaseCodeConverter<ServiceType> {
    protected ServiceTypeConverter() {
        super(ServiceType.class);
    }
}
