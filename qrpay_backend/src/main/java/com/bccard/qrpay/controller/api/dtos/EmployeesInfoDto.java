package com.bccard.qrpay.controller.api.dtos;

import com.bccard.qrpay.domain.common.code.MemberStatus;
import com.bccard.qrpay.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeesInfoDto {
    private String memberId;
    private String loginId;
    private Boolean permissionToCancel;
    private Boolean suspended;
    private String joinedAt;

    public static EmployeesInfoDto from(Member member) {
        return EmployeesInfoDto.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .permissionToCancel(member.getPermissionToCancel())
                .suspended(member.getStatus() == MemberStatus.SUSPENDED)
                .joinedAt(member.getCreatedAt())
                .build();
    }
}
