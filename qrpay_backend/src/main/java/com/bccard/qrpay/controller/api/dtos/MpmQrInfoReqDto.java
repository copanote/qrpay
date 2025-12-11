package com.bccard.qrpay.controller.api.dtos;


import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MpmQrInfoReqDto {
    @NotNull
    private PointOfInitMethod pim;
    private Long amount;
    private Long installment;
}

