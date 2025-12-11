package com.bccard.qrpay.domain.mpmqr;

import com.bccard.qrpay.domain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "TBMPMQRSNDAPLC")
public class MpmQrKitApplication extends BaseEntity implements Persistable<Long> {

    @Id
    @Column(name = "APLC_SEQ_NO")
    private Long id;

    @Column(name = "MER_MGMT_NO")
    private String merchantId;
    @Column(name = "MER_NM")
    private String merchantName;
    @Column(name = "NW_ADDR")
    private String newAddr;
    @Column(name = "ZP")
    private String zipCode;
    @Column(name = "HP_TEL_NO")
    private String phoneNo;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "QR_REF_ID")
    private String qrReferenceId;
    @Column(name = "ADD_APLC_YN")
    private String additionalApplication;
    @Column(name = "SND_STAT")
//    private QrKitShippingStatus status;
    private String status;

    @Column(name = "DONG_OVR_NW_ADDR")
    private String address1;
    @Column(name = "DONG_BLW_NW_ADDR")
    private String address2;
    @Column(name = "REGD_SND_DATE")
    private String regdSndDate;
    @Column(name = "REGD_NO")
    private String regdNo;

    @Column(name = "ADD_APLC_PE_ID")
    private String addAplcPeId;
    @Column(name = "ADD_APLC_RSON")
    private String addAplcRson;

    @Column(name = "APLC_CHNL_CLSS")
    private String aplcChnlClss;


    @Override
    public Long getId() {
        return id;
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