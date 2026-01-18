package com.bccard.qrpay.domain.transaction.dto;

import com.bccard.qrpay.domain.common.code.ServiceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

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
