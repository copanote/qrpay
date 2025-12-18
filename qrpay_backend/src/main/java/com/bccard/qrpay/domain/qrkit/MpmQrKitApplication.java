package com.bccard.qrpay.domain.qrkit;

import com.bccard.qrpay.domain.common.code.QrKitApplicationChannel;
import com.bccard.qrpay.domain.common.code.QrKitShippingStatus;
import com.bccard.qrpay.domain.common.converter.BooleanYnConverter;
import com.bccard.qrpay.domain.common.converter.QrKitApplicationChannelStatusConverter;
import com.bccard.qrpay.domain.common.converter.QrKitShippingStatusConverter;
import com.bccard.qrpay.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBMPMQRSNDAPLC")
public class MpmQrKitApplication extends BaseEntity implements Persistable<Long> {

    @Id
    @Column(name = "APLC_SEQ_NO", length = 10)
    private Long id;

    @Column(name = "MER_MGMT_NO", length = 9)
    private String merchantId;
    @Column(name = "MER_NM", length = 40)
    private String merchantName;
    @Column(name = "NW_ADDR", length = 200)
    private String address; //Not-Used
    @Column(name = "ZP", length = 6)
    private String zipCode;
    @Column(name = "HP_TEL_NO", length = 14)
    private String phoneNo;
    @Column(name = "EMAIL", length = 100)
    private String email; //Not-Used
    @Column(name = "QR_REF_ID", length = 25)
    private String qrReferenceId;
    @Column(name = "ADD_APLC_YN", length = 1)
    @Convert(converter = BooleanYnConverter.class)
    private Boolean extraApplication;
    @Column(name = "SND_STAT", length = 2)
    @Convert(converter = QrKitShippingStatusConverter.class)
    private QrKitShippingStatus status;

    @Column(name = "DONG_OVR_NW_ADDR", length = 100)
    private String address1;
    @Column(name = "DONG_BLW_NW_ADDR", length = 100)
    private String address2;
    @Column(name = "REGD_SND_DATE", length = 8)
    private String registeredMailSentAt;
    @Column(name = "REGD_NO", length = 15)
    private String registeredMailNo;

    @Column(name = "ADD_APLC_PE_ID", length = 40)
    private String additionalApplicantId;
    @Column(name = "ADD_APLC_RSON", length = 500)
    private String additionalApplicantReason;

    @Column(name = "APLC_CHNL_CLSS", length = 1)
    @Convert(converter = QrKitApplicationChannelStatusConverter.class)
    private QrKitApplicationChannel applicationChannel;

    @Override
    public Long getId() {
        return id;
    }


    @Builder(builderMethodName = "newQrKit")
    public MpmQrKitApplication(Long id, String merchantId, String merchantName,
                               String address1, String address2, String zipCode, String phoneNo,
                               String qrReferenceId, Boolean extraApplication,
                               QrKitShippingStatus status, QrKitApplicationChannel applicationChannel) {
        this.id = id;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.address1 = address1;
        this.address2 = address2;
        this.zipCode = zipCode;
        this.phoneNo = phoneNo;
        this.qrReferenceId = qrReferenceId;
        this.extraApplication = extraApplication;
        this.status = status;
        this.applicationChannel = applicationChannel;
    }
}


/**
 * 이름               널?       유형
 * ---------------- -------- -------------
 * APLC_SEQ_NO      NOT NULL NUMBER(10)
 * MER_MGMT_NO      NOT NULL VARCHAR2(9)
 * MER_NM                    VARCHAR2(40)
 * NW_ADDR                   VARCHAR2(200)
 * ZP                        VARCHAR2(6)
 * HP_TEL_NO                 VARCHAR2(14)
 * EMAIL                     VARCHAR2(100)
 * QR_REF_ID                 VARCHAR2(25)
 * ADD_APLC_YN               VARCHAR2(1)
 * REG_PE_ID                 VARCHAR2(40)
 * REG_ATON                  CHAR(14)
 * CORR_PE_ID                VARCHAR2(40)
 * CORR_ATON                 CHAR(14)
 * SND_STAT                  VARCHAR2(2)
 * DONG_OVR_NW_ADDR          VARCHAR2(100)
 * DONG_BLW_NW_ADDR          VARCHAR2(100)
 * REGD_SND_DATE             CHAR(8)
 * REGD_NO                   VARCHAR2(15)
 * ADD_APLC_PE_ID            VARCHAR2(40)
 * ADD_APLC_RSON             VARCHAR2(500)
 * APLC_CHNL_CLSS            CHAR(1)
 *
 */