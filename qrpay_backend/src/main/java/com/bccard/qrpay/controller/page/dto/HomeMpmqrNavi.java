package com.bccard.qrpay.controller.page.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeMpmqrNavi {
    private String merchantName;
    private String loginId;
    private boolean isAdmin;
}
