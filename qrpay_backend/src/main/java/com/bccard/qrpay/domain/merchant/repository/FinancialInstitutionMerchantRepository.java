package com.bccard.qrpay.domain.merchant.repository;

import com.bccard.qrpay.domain.merchant.FinancialInstitutionMerchant;
import com.bccard.qrpay.domain.merchant.FinancialInstitutionMerchantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialInstitutionMerchantRepository
        extends JpaRepository<FinancialInstitutionMerchant, FinancialInstitutionMerchantId> {}
