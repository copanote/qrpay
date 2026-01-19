package com.bccard.qrpay.domain.mpmqr;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.common.converter.PointOfInitMethodConverter;
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
@Table(name = "TBMPMQRCRETCTNT")
public class MpmQrPublication extends BaseEntity implements Persistable<String> {

    @Id
    @Column(name = "QR_REF_ID")
    private String qrReferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MER_MGMT_NO")
    private Merchant merchant;

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "MER_CDHD_NO")
    //    private Member member;
    @Column(name = "MER_CDHD_NO")
    private String memberId;

    @Column(name = "PUBL_CLSS")
    @Convert(converter = PointOfInitMethodConverter.class)
    private PointOfInitMethod pim = PointOfInitMethod.UNKNOWN;

    @Column(name = "TRNS_AMT")
    private Long amount;

    @Column(name = "QR_DATA", length = 4000)
    private String qrData;

    @Column(name = "STRT_ATON")
    private String startedAt;

    @Column(name = "AFFI_CO_ID")
    private String affiliateId;

    @Column(name = "AFFI_CO_REQ_VAL")
    private String affiliateRequestValue;

    @Override
    public String getId() {
        return qrReferenceId;
    }

    @Builder(builderMethodName = "createMpmqrPublication")
    public MpmQrPublication(
            String qrReferenceId,
            Merchant merchant,
            String memberId,
            PointOfInitMethod pim,
            Long amount,
            String qrData,
            String startedAt,
            String affiliateId,
            String affiliateRequestValue) {
        this.qrReferenceId = qrReferenceId;
        this.merchant = merchant;
        this.memberId = memberId;
        this.pim = pim;
        this.amount = amount;
        this.qrData = qrData;
        this.startedAt = startedAt;
        this.affiliateId = affiliateId;
        this.affiliateRequestValue = affiliateRequestValue;
    }
}
