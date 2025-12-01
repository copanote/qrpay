package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;
import jakarta.persistence.Converter;

@Converter
public class FinanceInstitutionConverter extends DatabaseCodeConverter<FinancialInstitution> {
    protected FinanceInstitutionConverter() {
        super(FinancialInstitution.class);
    }
}
