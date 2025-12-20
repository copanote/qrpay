package com.bccard.qrpay.controller.api.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class IdDupCheckReqDto {
    @NotNull
    private String id;
}
