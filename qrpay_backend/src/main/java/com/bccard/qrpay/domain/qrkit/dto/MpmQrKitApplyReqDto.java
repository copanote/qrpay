package com.bccard.qrpay.domain.qrkit.dto;

import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.merchant.Merchant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MpmQrKitApplyReqDto {
    private Member member;
    private Merchant merchant;
    private String reqMerchantName;
    private String address1;
    private String address2;
    private String zip;
    private String phoneNo;


}
