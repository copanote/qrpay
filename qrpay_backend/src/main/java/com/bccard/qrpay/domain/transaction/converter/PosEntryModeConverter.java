package com.bccard.qrpay.domain.transaction.converter;

import com.bccard.qrpay.domain.common.code.PosEntryMode;
import com.bccard.qrpay.domain.common.converter.DatabaseCodeConverter;
import jakarta.persistence.Converter;

@Converter
public class PosEntryModeConverter extends DatabaseCodeConverter<PosEntryMode> {
    protected PosEntryModeConverter() {
        super(PosEntryMode.class);
    }
}
