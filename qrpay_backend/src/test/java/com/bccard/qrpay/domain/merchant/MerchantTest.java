package com.bccard.qrpay.domain.merchant;

import com.bccard.qrpay.domain.common.code.*;
import com.bccard.qrpay.domain.merchant.fixture.MerchantFixture;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MerchantTest {

    @Test
    @DisplayName("[성공] Merchant 도메인(엔티티)객체 생성")
    void merchantCreate_success() {
        //Given //When
        Merchant merchant = Merchant.createNewMerchant()
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
        //Then
        assertThat(merchant).isNotNull();
    }

    @Test
    @DisplayName("[성공] Merchant 이름변경")
    void updateMerchantName_success() {
        //Given
        Merchant merchant = MerchantFixture.createMerchant();
        //When
        String changedName = "스타벅스_22";
        merchant.updateMerchantName(changedName);
        //Then
        assertThat(merchant.getMerchantName()).isEqualTo(changedName);
    }

    @Test
    @DisplayName("[실패] Merchant 14자리 이상 크기 이름변경 실패")
    void updateMerchantName_fail() {
        //Given
        Merchant merchant = MerchantFixture.createMerchant();
        //When
        String changedName = "스타벅스_1234567890";
        //Then
        assertThatThrownBy(() -> merchant.updateMerchantName(changedName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("[성공 ]봉사료[tip] 변경 성공")
    void updateTip_success() {
        //Given
        Merchant merchant = MerchantFixture.createMerchant();

        //when
        merchant.updateTip(BigDecimal.valueOf(15));

        //then
        assertThat(merchant.getTipRate()).isEqualTo(BigDecimal.valueOf(15));
    }

    @Test
    @DisplayName("[실패] 봉사료[tip] 변경 실패")
    void updateTip_fail() {
        //Given
        Merchant merchant = MerchantFixture.createMerchant();
        merchant.updateVat(BigDecimal.valueOf(50));
        //When Then
        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThatThrownBy(() -> merchant.updateTip(BigDecimal.valueOf(50)))
                            .isInstanceOf(IllegalStateException.class);
                    softly.assertThatThrownBy(() -> merchant.updateTip(BigDecimal.valueOf(-1)))
                            .isInstanceOf(IllegalStateException.class);
                    softly.assertThatThrownBy(() -> merchant.updateTip(BigDecimal.valueOf(101)))
                            .isInstanceOf(IllegalStateException.class);
                }
        );
    }

    @Test
    @DisplayName("[성공] 부가세[vat] 변경 성공")
    void updateVat_success() {
        //Given
        Merchant merchant = MerchantFixture.createMerchant();

        //when
        merchant.updateVat(BigDecimal.valueOf(15));

        //then
        assertThat(merchant.getVatRate()).isEqualTo(BigDecimal.valueOf(15));
    }

    @Test
    @DisplayName("[실패] 부가세[vat] 변경 실패")
    void updateVat_fail() {
        //Given
        Merchant merchant = MerchantFixture.createMerchant();
        merchant.updateTip(BigDecimal.valueOf(50));
        //When Then
        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThatThrownBy(() -> merchant.updateVat(BigDecimal.valueOf(50)))
                            .isInstanceOf(IllegalStateException.class);
                    softly.assertThatThrownBy(() -> merchant.updateVat(BigDecimal.valueOf(-1)))
                            .isInstanceOf(IllegalStateException.class);
                    softly.assertThatThrownBy(() -> merchant.updateVat(BigDecimal.valueOf(101)))
                            .isInstanceOf(IllegalStateException.class);
                }
        );
    }

    @Test
    @DisplayName("[성공] 가맹점상태 CANCEL 변경 성공")
    void changeStatusToCancel_success() {
        //Given
        Merchant merchant = MerchantFixture.createMerchant();
        //when
        merchant.cancel();
        //then
        assertThat(merchant.getMerchantStatus()).isEqualTo(MerchantStatus.CANCELLED);
        assertThat(merchant.getSecessionDate()).isNotNull();
    }

    @Test
    @DisplayName("[실패] 가맹점상태 CANCEL 변경 실패")
    void changeStatusToCancel_fail() {
    }


}
