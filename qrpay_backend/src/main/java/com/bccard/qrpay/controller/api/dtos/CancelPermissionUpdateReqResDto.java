package com.bccard.qrpay.controller.api.dtos;

import lombok.Getter;

@Getter
public class CancelPermissionUpdateReqResDto {
    private final boolean permissionToCancel;

    public CancelPermissionUpdateReqResDto(boolean permissionToCancel) {
        this.permissionToCancel = permissionToCancel;
    }

    public static CancelPermissionUpdateReqResDto of(Boolean b) {
        boolean permission = Boolean.TRUE.equals(b);
        return new CancelPermissionUpdateReqResDto(permission);
    }
}
