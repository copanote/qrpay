package com.bccard.qrpay.controller.api.dtos;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Schema(description = "MPMQR 생성 요청 정보")
@Getter
@ToString
public class MpmQrInfoReqDto {

    @Schema(description = "STATIC, DYNAMIC", example = "STATIC", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private PointOfInitMethod pim;

    @Schema(description = "mpmqr 금액, DYNAMIC일 때 필수, STATIC일 경우 무시한다.", minimum = "10", maximum = "99999999")
    private Long amount;

    @Schema(description = "할부개월수(00: 일시불), DYNAMIC일 때 필수, STATIC일 경우 무시한다.", minimum = "00", maximum = "24")
    private Long installment;
}
