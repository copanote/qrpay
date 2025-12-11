package com.bccard.qrpay.controller.api.dtos;


import com.bccard.qrpay.domain.merchant.Merchant;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class MerchantInfoResDto {
    private String merchantId;
    private String merchantName;

    private boolean enableVat;
    private int vatRate;

    private boolean enableTip;
    private int tipRate;

    public static MerchantInfoResDto from(Merchant merchant) {


        return null;

    }
}
