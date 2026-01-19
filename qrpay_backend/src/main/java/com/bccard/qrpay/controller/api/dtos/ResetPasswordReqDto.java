package com.bccard.qrpay.controller.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResetPasswordReqDto {
    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String smsRefId;

    public boolean confirmPasswordMatch() {
        return newPassword.equals(confirmPassword);
    }
}
