package com.bccard.qrpay.auth.contoller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RequestLogin {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}
