package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.MemberRole;
import jakarta.persistence.Converter;

@Converter
public class MemberRoleConverter extends DatabaseCodeConverter<MemberRole> {
    protected MemberRoleConverter() {
        super(MemberRole.class);
    }
}
