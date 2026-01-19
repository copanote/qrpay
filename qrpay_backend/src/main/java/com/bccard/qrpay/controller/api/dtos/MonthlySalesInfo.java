package com.bccard.qrpay.controller.api.dtos;

import com.bccard.qrpay.domain.common.code.ServiceType;
import com.bccard.qrpay.domain.transaction.dto.MonthlySalesDto;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class MonthlySalesInfo {
    private ServiceType serviceType;
    private BigDecimal totalAuthAmount;
    private BigDecimal totalRefundAmount;

    public static MonthlySalesInfo from(MonthlySalesDto dto) {
        MonthlySalesInfo.builder().serviceType(dto.getServiceType()).build();

        return null;
    }
}
