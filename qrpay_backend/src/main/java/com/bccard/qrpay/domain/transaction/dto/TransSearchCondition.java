package com.bccard.qrpay.domain.transaction.dto;

import com.bccard.qrpay.controller.api.dtos.TransHistorySearchDto;
import com.bccard.qrpay.domain.common.code.PaymentStatus;
import com.bccard.qrpay.domain.common.code.ServiceType;
import com.bccard.qrpay.domain.merchant.Merchant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransSearchCondition {
    private Merchant merchant;
    private String authNoLast4;
    private List<ServiceType> serviceType;
    private List<PaymentStatus> paymentStatus;
    private String startYmd;
    private String endYmd;

    public static TransSearchCondition from(Merchant m, TransHistorySearchDto dto) {
        return TransSearchCondition.builder()
                .merchant(m)
                .authNoLast4(dto.getAuthNoLast4())
                .serviceType(dto.getServiceType())
                .paymentStatus(dto.getPaymentStatus())
                .startYmd(dto.getStartYmd())
                .endYmd(dto.getEndYmd())
                .build();
    }

    public boolean isValidPeriod() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate start = LocalDate.parse(this.startYmd, formatter);
            LocalDate end = LocalDate.parse(this.endYmd, formatter);
            LocalDate now = LocalDate.now();

            // 1. 시작일이 종료일보다 늦으면 허용 안 함
            if (start.isAfter(end)) {
                return false;
            }

            // 2. 종료일이 오늘보다 미래인 경우 허용 안 함 (필요 시 선택)
            if (end.isAfter(now)) {
                return false;
            }

            // 3. 시작일이 현재로부터 1년 전보다 과거라면 허용 안 함
            LocalDate oneYearAgo = now.minusYears(1);
            if (start.isBefore(oneYearAgo)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            // 날짜 형식이 잘못된 경우(예: 20231345)
            return false;
        }
    }
}
