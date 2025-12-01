package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.MemberStatus;
import jakarta.persistence.Converter;

@Converter
public class MemberStatusConverter extends DatabaseCodeConverter<MemberStatus> {
    protected MemberStatusConverter() {
        super(MemberStatus.class);
    }
}
