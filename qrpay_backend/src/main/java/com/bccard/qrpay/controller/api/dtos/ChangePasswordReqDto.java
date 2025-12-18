package com.bccard.qrpay.controller.api.dtos;


import lombok.Getter;

@Getter
public class ChangePasswordReqDto {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;


    public boolean passwordResue() {
        return currentPassword.equals(newPassword);
    }

    public boolean confirmPasswordMatch() {
        return newPassword.equals(confirmPassword);
    }


}
