package com.bccard.qrpay.domain.log;

import com.bccard.qrpay.domain.common.code.MessageType;
import com.bccard.qrpay.domain.common.code.QrTransactionType;
import com.bccard.qrpay.domain.common.converter.MessageTypeConverter;
import com.bccard.qrpay.domain.common.converter.QrTransactionTypeConverter;
import com.bccard.qrpay.domain.common.entity.BaseEntity;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "TBMPMQRPAYPSTCLOG")
@Builder
public class QrpayLog extends BaseEntity implements Persistable<Long> {

    @Id
    @Column(name = "TRNS_SEQ_NO", nullable = false, length = 10)
    private Long id;

    @Column(name = "REG_MLSC_ATON", length = 17)
    private String createdAtSss;

    @Column(name = "CHNL_TRNS_UNIQ_NO", length = 14)
    private String transactionId;

    @Column(name = "AFFI_CO_TRNS_UNIQ_NO", length = 50)
    private String affiliateTransactionId;

    @Column(name = "UNIF_TRNS_UNIQ_NO", length = 50)
    private String unifiedQrTransactionId;

    @Column(name = "OBJ_INST_CODE", length = 4)
    private String instituteCode;

    @Column(name = "PSTC_NO", length = 50)
    private String messageId; // 전문번호

    @Convert(converter = MessageTypeConverter.class)
    @Column(name = "PROC_CLSS", length = 1)
    private MessageType messageType;

    @Column(name = "RSPN_CODE", length = 5)
    private String responseCode;

    @Column(name = "MER_MGMT_NO", length = 9)
    private String merchantId;

    @Column(name = "INTN_PROC_CODE", length = 10)
    private String internalResponseCode;

    @Convert(converter = QrTransactionTypeConverter.class)
    @Column(name = "QR_MER_TRNS_TYP")
    private QrTransactionType qrTransactionType;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "standardHeader", column = @Column(name = "RIX_HDR_VAL", length = 48)),
        @AttributeOverride(name = "header", column = @Column(name = "CMMN_HDR_VAL", length = 128)),
        @AttributeOverride(name = "body", column = @Column(name = "CHNL_DATA_1_VAL", length = 4000))
    })
    private LogMessage fepFldMessage;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "standardHeader", column = @Column(name = "FEP_STD_HDR_VAL", length = 4000)),
        @AttributeOverride(name = "header", column = @Column(name = "FEP_CMMN_HDR_VAL", length = 1000)),
        @AttributeOverride(name = "body", column = @Column(name = "FEP_CHNL_DATA_1_VAL", length = 4000))
    })
    private LogMessage logMessage;

    //    public QrpayLog(String transactionId, String affiliateTransactionId, String unifiedQrTransactionId, String
    // instituteCode,
    //                    String messageId, MessageType messageType, String responseCode, String merchantId, String
    // internalResponseCode,
    //                    QrTransactionType qrTransactionType, LogMessage fepFldMessage, LogMessage logMessage) {
    //        this.createdAtSss = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_MICROSEC);
    //        this.transactionId = transactionId;
    //        this.affiliateTransactionId = affiliateTransactionId;
    //        this.unifiedQrTransactionId = unifiedQrTransactionId;
    //        this.instituteCode = instituteCode;
    //        this.messageId = messageId;
    //        this.messageType = messageType;
    //        this.responseCode = responseCode;
    //        this.merchantId = merchantId;
    //        this.internalResponseCode = internalResponseCode;
    //        this.qrTransactionType = qrTransactionType;
    //        this.fepFldMessage = fepFldMessage;
    //        this.logMessage = logMessage;
    //    }

    public static QrpayLog smsNice(Long id, String log) {

        LogMessage logMessage = LogMessage.create().body(log).build();

        return QrpayLog.builder()
                .id(id)
                .createdAtSss(MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_MICROSEC))
                .logMessage(logMessage)
                .build();
    }

    @Override
    public Long getId() {
        return id;
    }
}

/*
TRNS_SEQ_NO          NOT NULL NUMBER(10)
REG_MLSC_ATON                 CHAR(17)
CHNL_TRNS_UNIQ_NO             VARCHAR2(14)
AFFI_CO_TRNS_UNIQ_NO          VARCHAR2(50)
UNIF_TRNS_UNIQ_NO             VARCHAR2(50)
OBJ_INST_CODE                 VARCHAR2(4)
PSTC_NO                       VARCHAR2(50)
PROC_CLSS                     VARCHAR2(1)
RIX_HDR_VAL                   VARCHAR2(48)
CMMN_HDR_VAL                  VARCHAR2(128)
CHNL_DATA_1_VAL               VARCHAR2(4000)
RSPN_CODE                     VARCHAR2(5)
REG_PE_ID                     VARCHAR2(40)
REG_ATON                      CHAR(14)
CORR_PE_ID                    VARCHAR2(40)
CORR_ATON                     CHAR(14)
MER_MGMT_NO                   VARCHAR2(9)
INTN_PROC_CODE                VARCHAR2(10)
FEP_STD_HDR_VAL               VARCHAR2(4000)
QR_MER_TRNS_TYP               VARCHAR2(2)
FEP_CMMN_HDR_VAL              VARCHAR2(1000)
FEP_CHNL_DATA_1_VAL           VARCHAR2(4000)
 */
