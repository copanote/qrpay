package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.PosEntryMode;
import jakarta.persistence.Converter;

@Converter
public class PosEntryModeConverter extends DatabaseCodeConverter<PosEntryMode> {
    protected PosEntryModeConverter() {
        super(PosEntryMode.class);
    }
}
