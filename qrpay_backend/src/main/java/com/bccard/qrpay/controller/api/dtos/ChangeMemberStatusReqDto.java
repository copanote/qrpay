package com.bccard.qrpay.controller.api.dtos;

import com.bccard.qrpay.domain.common.code.MemberStatus;
import lombok.Getter;

@Getter
public class ChangeMemberStatusReqDto {
    private MemberStatus requestStatus;
}
