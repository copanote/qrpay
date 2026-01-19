package com.bccard.qrpay.domain.transaction.dto;

import com.bccard.qrpay.domain.common.code.ServiceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MonthlySalesDto {
    @JsonIgnore
    private String yearMonth;

    private ServiceType serviceType;
    private BigDecimal totalAuthAmount;
    private BigDecimal totalRefundAmount;
}
