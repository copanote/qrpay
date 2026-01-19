package com.bccard.qrpay.external.nice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class NiceSmsResultDto {
    private String referenceId;
    //    private String mobileNo;
    //    private String di;
    //    private String mobileCompany;
    private String ci;
    //    private String utf8Name;
    //    private String gender;
    //    private String resSeq;
    //    private String birthDate;
    //    private String authType;
    //    private String name;

    public static NiceSmsResultDto of(String referenceId, String ci) {
        return NiceSmsResultDto.builder().referenceId(referenceId).ci(ci).build();
    }
}
