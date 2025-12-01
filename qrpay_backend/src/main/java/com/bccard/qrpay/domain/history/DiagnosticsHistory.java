package com.bccard.qrpay.domain.history;

public class DiagnosticsHistory {

    private Long id;
    private String trndTraceNo;

    private String rnmMlscTime; //송신밀리세컨시간
    private String recpMlscTime; //수신밀리세컨시간

    private String pstcProcMlscTime; //전문처리밀리세컨시간

    private String sysClss; //시스템구분코드

    private String url;
    private String procClss; //처리구분코드
    private String rspnCode; //응답코드
    

}


/**
 * MPM 모니터링 이력
 * 이름                  널?       유형
 * ------------------- -------- -------------
 * TRNS_SEQ_NO         NOT NULL NUMBER(10)
 * TRNS_TRACE_NO       NOT NULL VARCHAR2(26)
 * REG_PE_ID                    VARCHAR2(40)
 * REG_ATON            NOT NULL CHAR(14)
 * CORR_PE_ID                   VARCHAR2(40)
 * CORR_ATON                    CHAR(14)
 * TRNM_MLSC_TIME               CHAR(9)
 * RECP_MLSC_TIME               CHAR(9)
 * PSTC_PROC_MLSC_TIME          CHAR(9)
 * SYS_CLSS            NOT NULL VARCHAR2(1)
 * URL                 NOT NULL VARCHAR2(500)
 * PROC_CLSS                    VARCHAR2(1)
 * RSPN_CODE                    VARCHAR2(5)
 */