package com.bccard.qrpay.domain.merchant.fixture;

import com.bccard.qrpay.domain.common.code.*;
import com.bccard.qrpay.domain.merchant.FinancialInstitutionMerchant;
import com.bccard.qrpay.domain.merchant.Merchant;

import java.util.UUID;

public class MerchantFixture {

    public static Merchant createMerchant() {

        return Merchant.createNewMerchant()
                .merchantId(UUID.randomUUID().toString().substring(0, 9))
                .merchantStatus(MerchantStatus.ACTIVE)
                .merchantType(MerchantType.BASIC)
                .merchantRegister(MerchantRegister.MERCHANT)
                .mcc("111")
                .businessNo("1234567890")
                .merchantName("스타벅스")
                .merchantEnglishName("Starbucks")
                .cityName("서울")
                .cityEnglishName("seoul")
                .merchantZipCode("01234")
                .merchantTelAreaNo("02")
                .merchantTelMiddleNo("123")
                .merchantTelLastNo("4567")
                .representativeName("Shin")
                .representativeBirthDay("991010")
                .representativeEmail("test@gmail.com")
                .registrationRequestor(FinancialInstitution.BCCARD)
                .acquisitionMethod(AcquisitionMethod.EDI)
                .build();
    }

    public static FinancialInstitutionMerchant createFiMerchant() {
        Merchant m = createMerchant();
        FinancialInstitution fi = FinancialInstitution.BCCARD; // 예시 Enum 값
        String fiNo = "FI12345678";
        String fiName = "카카오뱅크_가맹점";

        return FinancialInstitutionMerchant.createNewFinancialInstituteMerchant()
                .merchant(m)
                .financialInstitution(fi)
                .fiMerchantNo(fiNo)
                .fiMerchantName(fiName)
                .build();
    }

    public static FinancialInstitutionMerchant createFiMerchant(Merchant merchant) {
        FinancialInstitution fi = FinancialInstitution.BCCARD; // 예시 Enum 값
        String fiNo = "FI12345678";
        String fiName = "카카오뱅크_가맹점";

        return FinancialInstitutionMerchant.createNewFinancialInstituteMerchant()
                .merchant(merchant)
                .financialInstitution(fi)
                .fiMerchantNo(fiNo)
                .fiMerchantName(fiName)
                .build();
    }
}
