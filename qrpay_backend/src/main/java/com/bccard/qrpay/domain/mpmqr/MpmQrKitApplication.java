package com.bccard.qrpay.domain.mpmqr;

import com.bccard.qrpay.domain.common.code.QrKitShippingStatus;
import com.bccard.qrpay.domain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBMPMQRSNDAPLC")
public class MpmQrKitApplication extends BaseEntity {

    @Id
    @Column(name = "APLC_SEQ_NO")
    private Long id;

    private String merchantId;
    private String merchantName;

    private String zipCode;
    private String phoneNo;
    private String email;
    private String qrReferenceId;
    private Boolean additionalApplication;
    private QrKitShippingStatus status;

    private String address1;
    private String address2;

    private String regdSndDate;
    private String regdNo;

    private String addAplcPeId;
    private String addAplcRson;

    private String aplcChnlClss;
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
