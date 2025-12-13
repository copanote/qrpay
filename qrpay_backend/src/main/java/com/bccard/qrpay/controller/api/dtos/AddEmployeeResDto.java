package com.bccard.qrpay.controller.api.dtos;


import com.bccard.qrpay.domain.common.code.MemberStatus;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.Permission;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class AddEmployeeResDto {
    private String memberId;
    private String loginId;
    private MemberStatus status;
    private String createdAt;
    private List<Permission> permissions;

    public static AddEmployeeResDto from(Member member) {

        List<Permission> permissionList = new ArrayList<>();
        if (member.getPermissionToCancel()) {
            permissionList.add(Permission.CANCEL);
        }

        return AddEmployeeResDto.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .permissions(permissionList)
                .build();
    }

}
