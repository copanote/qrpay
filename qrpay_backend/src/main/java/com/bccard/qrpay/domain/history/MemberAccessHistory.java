package com.bccard.qrpay.domain.history;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;

public class MemberAccessHistory {

    private Long id;

    private String memberId;
    private String menuClss; //make enum
    private String responseCode;
    private String year;  //2025
    private String yearMonth; //202511
    private String day; //07
    private String time; //160521
    private String loginId;
    private FinancialInstitution financialInstitution;
    private String cardCoMerMgmgNo;
    

}


/**
 * 가맹점회원접속이력
 * 이름                  널?       유형
 * ------------------- -------- ------------
 * CONN_HST_SEQ_NO     NOT NULL NUMBER(10)
 * MER_CDHD_NO                  VARCHAR2(9)
 * MENU_CLSS           NOT NULL CHAR(2)
 * RSPN_CODE                    VARCHAR2(5)
 * REG_YR                       CHAR(4)
 * REG_YM                       CHAR(6)
 * REG_DAY                      CHAR(2)
 * REG_TIME                     CHAR(6)
 * REG_PE_ID                    VARCHAR2(40)
 * REG_ATON            NOT NULL CHAR(14)
 * CORR_PE_ID                   VARCHAR2(40)
 * CORR_ATON                    CHAR(14)
 * MER_CDHD_ID                  VARCHAR2(40)
 * FNST_COPR_CODE               VARCHAR2(3)
 * CARD_CO_MER_MGMT_NO          VARCHAR2(15)
 */