package com.bccard.qrpay.domain.transaction;

public class MerchantMonthlyApproval {
    private String merMgmtNo;  //pk
    private String yaerMonth;  //pk
    private Long authAmt;
    private Long cnclAmt;
}

/**
 * MPM가맹점 월별 승인 내역
 * 이름           널?       유형
 * ------------ -------- ------------
 * MER_MGMT_NO   NOT NULL VARCHAR2(9)
 * TRNS_YM       NOT NULL CHAR(6)
 * AUTH_AMT              NUMBER(15)
 * CNCL_AMT              NUMBER(15)
 * REG_PE_ID             VARCHAR2(40)
 * REG_ATON              CHAR(14)
 * CORR_PE_ID            VARCHAR2(40)
 * CORR_ATON             CHAR(14)
 *
 */