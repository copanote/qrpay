package com.bccard.qrpay.domain.transaction;

import com.bccard.qrpay.domain.common.code.AuthorizeType;
import com.bccard.qrpay.domain.transaction.converter.AuthorizedConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import java.io.Serial;
import java.io.Serializable;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class TransactionId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "TRNS_UNIQ_NO")
    @EqualsAndHashCode.Include
    private String transactionId;

    @Column(name = "AFFI_CO_TRNS_UNIQ_NO")
    @EqualsAndHashCode.Include
    private String affiliateTransactionId;

    @Column(name = "TRNS_KND_CLSS")
    @EqualsAndHashCode.Include
    @Convert(converter = AuthorizedConverter.class)
    private AuthorizeType authorizeType;

    @Builder(builderMethodName = "create")
    public TransactionId(String transactionId, String affiliateTransactionId, AuthorizeType authorizeType) {
        this.transactionId = transactionId;
        this.affiliateTransactionId = affiliateTransactionId;
        this.authorizeType = authorizeType;
    }
}

/**
 * 이름                   널?       유형
 * -------------------- -------- -------------
 * TRNS_UNIQ_NO         NOT NULL VARCHAR2(12)
 * AFFI_CO_TRNS_UNIQ_NO NOT NULL VARCHAR2(50)
 * TRNS_ATON                     CHAR(14)
 * EPAN_NO                       VARCHAR2(44)
 * MPAN_NO                       VARCHAR2(19)
 * MER_MGMT_NO          NOT NULL VARCHAR2(9)
 * MER_CDHD_NO                   VARCHAR2(9)
 * FNST_COPR_CODE                VARCHAR2(3)
 * TRNS_KND_CLSS        NOT NULL VARCHAR2(2)
 * SVC_CLSS             NOT NULL VARCHAR2(1)
 * REQ_INST_CODE                 VARCHAR2(4)
 * TRNS_STAT            NOT NULL CHAR(2)
 * CARD_CO_AUTH_NO               VARCHAR2(8)
 * AFFI_AUTH_NO                  VARCHAR2(12)
 * AUTH_ATON                     CHAR(14)
 * TRNS_AMT                      NUMBER(18,3)
 * PAMT                          NUMBER(18,3)
 * TAX                           NUMBER(15)
 * SVC_FEE                       NUMBER(18,3)
 * DC_BEF_AMT                    NUMBER(18,3)
 * DC_AFTR_AMT                   NUMBER(18,3)
 * DC_AMT                        NUMBER(18,3)
 * REG_PE_ID                     VARCHAR2(40)
 * REG_ATON             NOT NULL CHAR(14)
 * CORR_PE_ID                    VARCHAR2(40)
 * CORR_ATON                     CHAR(14)
 * QR_REF_ID                     VARCHAR2(25)
 * RSPN_CODE                     VARCHAR2(5)
 * VALD_LIM                      CHAR(6)
 * UNIF_TRNS_UNIQ_NO             VARCHAR2(50)
 * REFD_AMT                      NUMBER(15)
 * AUTH_CLSS                     VARCHAR2(2)
 * INS_TRM                       NUMBER(2)
 * PNT_CLSS                      CHAR(2)
 * RTW_MPAN_NO                   VARCHAR2(19)
 * ETR_MODE_CD                   VARCHAR2(4)
 * CRNCY_CODE                    VARCHAR2(3)
 * QR_MER_TRNS_TYP               VARCHAR2(2)
 * MNY_TYP_NO                    VARCHAR2(4)
 * REQ_MNY                       NUMBER(15)
 * ENC_PAN_NO                    VARCHAR2(100)
 */
