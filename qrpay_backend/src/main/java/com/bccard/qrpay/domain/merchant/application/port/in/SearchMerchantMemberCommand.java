package com.bccard.qrpay.domain.merchant.application.port.in;

import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.MemberStatus;
import lombok.Builder;

@Builder
public record SearchMerchantMemberCommand(
        String merchantId,
        MemberRole role,
        MemberStatus status
) {
}
