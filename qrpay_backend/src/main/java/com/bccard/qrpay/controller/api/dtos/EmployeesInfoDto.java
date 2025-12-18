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
    private MemberStatus status;


    public static EmployeesInfoDto from(Member member) {
        return EmployeesInfoDto.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .permissionToCancel(member.getPermissionToCancel())
                .status(member.getStatus())
                .build();
    }
}
