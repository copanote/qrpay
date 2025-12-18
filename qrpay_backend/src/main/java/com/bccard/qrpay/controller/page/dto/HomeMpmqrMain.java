package com.bccard.qrpay.controller.page.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeMpmqrMain {
    private String merchantName;
    private String qrBase64Image;
    private boolean isAdmin;
}
