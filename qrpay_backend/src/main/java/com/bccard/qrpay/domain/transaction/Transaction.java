package com.bccard.qrpay.domain.transaction;

import com.bccard.qrpay.domain.common.code.*;
import com.bccard.qrpay.domain.common.converter.*;
import com.bccard.qrpay.domain.common.entity.BaseEntity;
import com.bccard.qrpay.domain.merchant.Merchant;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBMPMTRNSCTNT")
@IdClass(TransactionId.class)
@ToString(exclude = {"merchant"})
public class Transaction extends BaseEntity implements Persistable<TransactionId> {

    @Id
    @Column(name = "TRNS_UNIQ_NO", length = 12)
    private String transactionId;

    @Id
    @Column(name = "AFFI_CO_TRNS_UNIQ_NO", length = 50)
    private String affiliateTransactionId;

    @Column(name = "TRNS_ATON", length = 14)
    private String transactionAt; // TRNS_ATON

    @Column(name = "EPAN_NO", length = 100)
    private String ePan;

    @Column(name = "ENC_PAN_NO", length = 100)
    private String encrypedPan;

    @Column(name = "MPAN_NO", length = 19)
    private String maskedPan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MER_MGMT_NO", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Merchant merchant;

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "MER_CDHD_NO", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //    @NotFound(action = NotFoundAction.IGNORE)
    //    private Member member;

    @Column(name = "MER_CDHD_NO")
    private String memberId;

    @Column(name = "FNST_COPR_CODE")
    @Convert(converter = FinanceInstitutionConverter.class)
    private FinancialInstitution fi = FinancialInstitution.UNKNOWN;

    @Id
    @Column(name = "TRNS_KND_CLSS")
    @Convert(converter = AuthorizedConverter.class)
    private AuthorizeType authorizeType = AuthorizeType.UNKNOWN;

    @Column(name = "SVC_CLSS")
    @Convert(converter = ServiceTypeConverter.class)
    private ServiceType serviceType;

    @Column(name = "REQ_INST_CODE", length = 4)
    private String reqInstituteCode;

    @Column(name = "TRNS_STAT")
    @Convert(converter = PaymentStatusConverter.class)
    private PaymentStatus paymentStatus = PaymentStatus.UNKNOWN;

    @Column(name = "CARD_CO_AUTH_NO", length = 8)
    private String approvalNo;

    @Column(name = "AFFI_AUTH_NO", length = 12)
    private String affiliateApprovalNo; //

    @Column(name = "AUTH_ATON", length = 14)
    private String approvedAt; // authNo

    @Column(name = "TRNS_AMT", precision = 18, scale = 3)
    private BigDecimal transactionAmount; // trnsAmt

    @Column(name = "PAMT", precision = 18, scale = 3)
    private BigDecimal principalAmount; // pAmt

    @Column(name = "TAX", precision = 15, scale = 0)
    private BigDecimal tax;

    @Column(name = "SVC_FEE", precision = 18, scale = 3)
    private BigDecimal serviceFee;

    @Column(name = "DC_BEF_AMT", precision = 18, scale = 3)
    private BigDecimal amountBeforeDiscount;

    @Column(name = "DC_AFTR_AMT", precision = 18, scale = 3)
    private BigDecimal amountAfterDiscount;

    @Column(name = "DC_AMT", precision = 18, scale = 3)
    private BigDecimal discountedAmount;

    @Column(name = "QR_REF_ID", length = 25)
    private String qrReferencdId;

    @Column(name = "RSPN_CODE", length = 5)
    private String responseCode;

    @Column(name = "VALD_LIM", length = 6)
    private String expirationDate; // YYYYDD

    @Column(name = "UNIF_TRNS_UNIQ_NO", length = 50)
    private String unifiedQrTransactionId;

    @Column(name = "REFD_AMT", precision = 15)
    private BigDecimal refundAmountToMerchant;

    @Column(name = "AUTH_CLSS")
    @Convert(converter = ApprovedTypeConverter.class)
    private ApprovedType approvedType = ApprovedType.UNKNOWN;

    @Column(name = "INS_TRM", precision = 2)
    private Long instalmentPeriod;

    @Column(name = "PNT_CLSS", length = 2)
    private String pointClass;

    @Column(name = "RTW_MPAN_NO", length = 19)
    private String maskedPanForReceipt;

    @Column(name = "ETR_MODE_CD")
    @Convert(converter = PosEntryModeConverter.class)
    private PosEntryMode pem = PosEntryMode.UNKNOWN;

    @Column(name = "CRNCY_CODE", length = 3)
    private String currencyCode;

    @Column(name = "QR_MER_TRNS_TYP")
    @Convert(converter = QrTransactionTypeConverter.class)
    private QrTransactionType qrTransactionType;

    @Column(name = "MNY_TYP_NO", length = 4)
    private String moneyType; // 0001 페이북머니

    @Column(name = "REQ_MNY", precision = 15)
    private BigDecimal requestMoneyAmount;

    @Override
    public TransactionId getId() {
        return TransactionId.create()
                .transactionId(transactionId)
                .affiliateTransactionId(affiliateTransactionId)
                .authorizeType(authorizeType)
                .build();
    }

    @Builder
    public Transaction(
            String transactionId,
            String affiliateTransactionId,
            Merchant merchant,
            AuthorizeType authorizeType,
            ServiceType serviceType,
            PaymentStatus paymentStatus) {
        this.transactionId = transactionId;
        this.affiliateTransactionId = affiliateTransactionId;
        this.merchant = merchant;
        this.authorizeType = authorizeType;
        this.serviceType = serviceType;
        this.paymentStatus = paymentStatus;
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
