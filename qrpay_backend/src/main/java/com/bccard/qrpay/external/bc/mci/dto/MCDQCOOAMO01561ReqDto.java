package com.bccard.qrpay.external.bc.mci.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MCDQCOOAMO01561ReqDto {
    private String merNoV9;
    private String biznoV10;

    public static MCDQCOOAMO01561ReqDto create(String bcMerchantNo) {
        return MCDQCOOAMO01561ReqDto.builder()
                .merNoV9(bcMerchantNo)
                .build();
    }
}
