package com.bccard.qrpay.domain.merchant;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;
import com.bccard.qrpay.domain.common.converter.FinanceInstitutionConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class FinancialInstitutionMerchantId implements Serializable {

    @EqualsAndHashCode.Include
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MER_MGMT_NO")
    private Merchant merchant;

    @EqualsAndHashCode.Include
    @Convert(converter = FinanceInstitutionConverter.class)
    @Column(name = "FNST_COPR_CODE", length = 3, nullable = false)
    private FinancialInstitution financialInstitution;

    public static FinancialInstitutionMerchantId of(Merchant merchant, FinancialInstitution financialInstitution) {
        return new FinancialInstitutionMerchantId(merchant, financialInstitution);
    }

    private FinancialInstitutionMerchantId(Merchant merchant, FinancialInstitution financialInstitution) {
        this.merchant = merchant;
        this.financialInstitution = financialInstitution;
    }
}
