package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.AcquisitionMethod;
import jakarta.persistence.Converter;

@Converter
public class AcquisitionMethodConverter extends DatabaseCodeConverter<AcquisitionMethod> {
    public AcquisitionMethodConverter() {
        super(AcquisitionMethod.class);
    }
}
