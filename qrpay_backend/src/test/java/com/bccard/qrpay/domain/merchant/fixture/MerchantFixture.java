package com.bccard.qrpay.domain.merchant.fixture;

import com.bccard.qrpay.domain.common.code.*;
import com.bccard.qrpay.domain.merchant.Merchant;

import java.util.UUID;

public class MerchantFixture {

    public static Merchant create() {

        return Merchant.createNewMerchant()
                .merchantId(UUID.randomUUID().toString())
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
}
