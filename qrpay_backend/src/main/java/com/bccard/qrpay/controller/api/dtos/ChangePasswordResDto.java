package com.bccard.qrpay.controller.api.dtos;

import com.bccard.qrpay.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangePasswordResDto {
    private String changedAt;

    public static ChangePasswordResDto of(Member member) {
        return ChangePasswordResDto.builder()
                .changedAt(member.getPasswordChangedAt())
                .build();
    }
}
