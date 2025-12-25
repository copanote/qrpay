package com.bccard.qrpay.controller.page.dto;

import com.bccard.qrpay.controller.api.dtos.QrKitApplicationDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QrKitStatusResDto {
    private String responseCode;
    private String message;
    private List<QrKitApplicationDto> list;
}
