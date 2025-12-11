package com.bccard.qrpay.controller.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class MerchantNameChangeReqDto {
    @Length(min = 1, max = 14)
    @NotBlank
    private String name;
}
