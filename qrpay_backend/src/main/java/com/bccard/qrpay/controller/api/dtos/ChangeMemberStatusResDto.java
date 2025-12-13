package com.bccard.qrpay.controller.api.dtos;


import com.bccard.qrpay.domain.common.code.MemberStatus;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ChangeMemberStatusResDto {
    private MemberStatus status;
    private String changedAt;

    public static ChangeMemberStatusResDto of(MemberStatus changedStatus, String changedAt) {
        return ChangeMemberStatusResDto
                .builder()
                .status(changedStatus)
                .changedAt(changedAt)
                .build();
    }
}
