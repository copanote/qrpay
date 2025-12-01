package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import jakarta.persistence.Converter;

@Converter
public class PointOfInitMethodConverter extends DatabaseCodeConverter<PointOfInitMethod> {
    protected PointOfInitMethodConverter() {
        super(PointOfInitMethod.class);
    }
}
