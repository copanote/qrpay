package com.bccard.qrpay.domain.history;

import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.MemberStatus;
import com.bccard.qrpay.domain.common.converter.BooleanYnConverter;
import com.bccard.qrpay.domain.common.converter.MemberRoleConverter;
import com.bccard.qrpay.domain.common.converter.MemberStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;

public class MemberHistory {

    private Long id;

    @Column(name = "MER_CDHD_ID", length = 40)
    private String memberId;

    @Column(name = "EPASWD", length = 64, nullable = false)
    private String encryptedPassword;

    @Column(name = "BAS_YN")
    @Convert(converter = MemberRoleConverter.class)
    private MemberRole role = MemberRole.UNDEFINED;

    @Column(name = "CDHD_STAT", nullable = false)
    @Convert(converter = MemberStatusConverter.class)
    private MemberStatus status = MemberStatus.UNKNOWN;

    @Column(name = "STIP_CNST_INFO", length = 100)
    private String termsAgreeInfo;

    @Column(name = "AUTH_CNCL_ABLE_YN", length = 1)
    @Convert(converter = BooleanYnConverter.class)
    private Boolean authCnclAbleYn;
}

/**
 * 가맹점 회원 정보 이력
 * 이름                널?       유형
 * ----------------- -------- -------------
 * HST_SEQ_NO        NOT NULL NUMBER(10)
 * MER_CDHD_NO       NOT NULL VARCHAR2(9)
 * EPASWD                     VARCHAR2(64)
 * BAS_YN                     VARCHAR2(1)
 * CDHD_STAT                  VARCHAR2(2)
 * STIP_CNST_INFO             VARCHAR2(100)
 * AUTH_CNCL_ABLE_YN          VARCHAR2(1)
 * REG_PE_ID                  VARCHAR2(40)
 * REG_ATON                   CHAR(14)
 * CORR_PE_ID                 VARCHAR2(40)
 * CORR_ATON                  CHAR(14)
 */
