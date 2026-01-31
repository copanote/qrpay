package com.bccard.qrpay.domain.member.repository;

import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.MemberStatus;
import lombok.Builder;


@Builder
public record MemberSearchCondition(
        String merchantId,
        MemberRole role,
        MemberStatus status) {
}
