package com.bccard.qrpay.controller.page.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ResponseHomeMpmqr {
    private final String merchantName;
    private final String qrBase64Image;
    private final boolean isAdmin;
}
