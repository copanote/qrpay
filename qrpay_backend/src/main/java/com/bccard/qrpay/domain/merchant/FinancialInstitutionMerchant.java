package com.bccard.qrpay.domain.merchant;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;
import com.bccard.qrpay.domain.common.converter.FinanceInstitutionConverter;
import com.bccard.qrpay.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(FinancialInstitutionMerchantId.class)
@Table(name = "TBMPMMERMGMTNOMAPPINFO")
public class FinancialInstitutionMerchant extends BaseEntity implements Persistable<FinancialInstitutionMerchantId> {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MER_MGMT_NO", insertable = false, updatable = false)
    private Merchant merchant;

    @Id
    @Column(name = "FNST_COPR_CODE", length = 3, nullable = false)
    @Convert(converter = FinanceInstitutionConverter.class)
    private FinancialInstitution financialInstitution;

    @Column(name = "CARD_CO_MER_MGMT_NO", length = 15, nullable = false)
    private String fiMerchantNo;

    @Column(name = "CARD_CO_MER_NM", length = 40)
    private String fiMerchantName;

    @Builder(builderMethodName = "createNewFinancialInstituteMerchant")
    public FinancialInstitutionMerchant(
            Merchant merchant, FinancialInstitution financialInstitution, String fiMerchantNo, String fiMerchantName) {
        this.merchant = merchant;
        this.financialInstitution = financialInstitution;
        this.fiMerchantNo = fiMerchantNo;
        this.fiMerchantName = fiMerchantName;
        this.merchant.addFinancialInstitute(this);
    }

    @Override
    public FinancialInstitutionMerchantId getId() {
        return FinancialInstitutionMerchantId.of(merchant, financialInstitution);
    }
}
