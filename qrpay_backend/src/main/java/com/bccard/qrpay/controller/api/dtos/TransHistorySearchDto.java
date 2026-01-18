package com.bccard.qrpay.controller.api.dtos;

import com.bccard.qrpay.domain.common.code.PaymentStatus;
import com.bccard.qrpay.domain.common.code.ServiceType;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class TransHistorySearchDto {
    @Pattern(regexp = "\\d{8}", message = "날짜 형식은 yyyyMMdd 8자리여야 합니다.")
    private String startYmd;
    @Pattern(regexp = "\\d{8}", message = "날짜 형식은 yyyyMMdd 8자리여야 합니다.")
    @DateTimeFormat(pattern = "yyyyMMdd")
    private String endYmd;
    private String authNoLast4;
    private List<PaymentStatus> paymentStatus;
    private List<ServiceType> serviceType;
}
