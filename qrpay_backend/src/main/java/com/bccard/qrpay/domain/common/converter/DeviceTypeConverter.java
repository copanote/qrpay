package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.DeviceType;
import jakarta.persistence.Converter;

@Converter
public class DeviceTypeConverter extends DatabaseCodeConverter<DeviceType> {
    protected DeviceTypeConverter() {
        super(DeviceType.class);
    }
}
