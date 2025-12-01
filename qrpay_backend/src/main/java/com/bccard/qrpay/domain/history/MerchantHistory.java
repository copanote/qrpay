package com.bccard.qrpay.domain.history;

import com.bccard.qrpay.domain.common.code.MerchantStatus;
import com.bccard.qrpay.domain.common.converter.MerchantStatusConverter;
import com.bccard.qrpay.domain.common.entity.BaseEntity;
import com.bccard.qrpay.domain.merchant.Merchant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBMPMMERBASINFOHST")
public class MerchantHistory extends BaseEntity implements Persistable<Long> {

    @Id
    private Long id;

    @Column(name = "MER_MGMT_NO", length = 9, nullable = false)
    private String merchantId;

    @Column(name = "MER_NM")
    private String merchantName;

    @Column(name = "MER_STAT")
    @Convert(converter = MerchantStatusConverter.class)
    private MerchantStatus merchantStatus;

    @Column(name = "MO_LIM_AMT", precision = 15)
    private Long monthlyLimitAmount;

    @Column(name = "ONCE_LIM_AMT", precision = 15)
    private Long singleLimitAmount;

    @Override
    public Long getId() {
        return id;
    }

    @Builder
    public MerchantHistory(Merchant merchant) {
        this.merchantId = merchant.getMerchantId();
        this.merchantName = merchant.getMerchantName();
        this.merchantStatus = merchant.getMerchantStatus();
        this.monthlyLimitAmount = merchant.getMonthlyLimitAmount();
        this.singleLimitAmount = merchant.getSingleLimitAmount();
    }
}

/**
 * 가맹점 기본 정보이력
 * 이름           널?       유형
 * ------------ -------- ------------
 * HST_SEQ_NO   NOT NULL NUMBER(10)
 * MER_MGMT_NO  NOT NULL VARCHAR2(9)
 * MER_STAT              CHAR(2)
 * MO_LIM_AMT            NUMBER(15)
 * ONCE_LIM_AMT          NUMBER(15)
 * REG_PE_ID             VARCHAR2(40)
 * REG_ATON     NOT NULL CHAR(14)
 * CORR_PE_ID            VARCHAR2(40)
 * CORR_ATON             CHAR(14)
 * MER_NM                VARCHAR2(40)
 * <p>
 * BCDBA.SEQ_MPMMERCDHDCONNHST.NEXTVAL
 * BCDBA.SEQ_MPMMERCDHDINFOHST.NEXTVAL
 * BCDBA.SEQ_MPMMONIHST.NEXTVAL
 *
 */
