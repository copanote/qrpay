package com.bccard.qrpay.controller.api.dtos;


import com.bccard.qrpay.domain.member.Permission;
import lombok.Getter;

import java.util.List;

@Getter
public class AddEmployeeReqDto {
    private String loginId;
    private String password;
    private String confirmPassword;
    private List<Permission> permissions;
}
