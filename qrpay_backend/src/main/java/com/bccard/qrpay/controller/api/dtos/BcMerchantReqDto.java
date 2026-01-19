package com.bccard.qrpay.controller.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BcMerchantReqDto {
    @NotBlank
    private String bizNo;

    private String nextKey;
}
