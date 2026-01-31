package com.bccard.qrpay.domain.merchant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;
import com.bccard.qrpay.domain.merchant.fixture.MerchantFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FinancialInstitutionMerchantTest {

    @Test
    @DisplayName("[성공]FinancialInstitutionMerchant 도메인 객체 생성 정상")
    void createFinancialInstitutionMerchantTest() {
        // given
        // Mock으로 대체하거나 최소 정보만 세팅
        Merchant mockMerchant = mock(Merchant.class);
        FinancialInstitution fi = FinancialInstitution.BCCARD; // 예시 Enum 값
        String fiNo = "FI12345678";
        String fiName = "카카오뱅크_가맹점";

        // when
        FinancialInstitutionMerchant fiMerchant = FinancialInstitutionMerchant.createNewFinancialInstituteMerchant()
                .merchant(mockMerchant)
                .financialInstitution(fi)
                .fiMerchantNo(fiNo)
                .fiMerchantName(fiName)
                .build();

        // then
        assertThat(fiMerchant.getMerchant()).isEqualTo(mockMerchant);
        assertThat(fiMerchant.getFinancialInstitution()).isEqualTo(fi);
        assertThat(fiMerchant.getFiMerchantNo()).isEqualTo(fiNo);
        assertThat(fiMerchant.getFiMerchantName()).isEqualTo(fiName);
    }

    @Test
    @DisplayName("[성공] 복합키 ID 로직")
    void getIdTest() {
        // given
        Merchant merchant = MerchantFixture.createMerchant();
        FinancialInstitution fi = FinancialInstitution.BCCARD;

        FinancialInstitutionMerchant fiMerchant = FinancialInstitutionMerchant.createNewFinancialInstituteMerchant()
                .merchant(merchant)
                .financialInstitution(fi)
                .build();

        // when
        FinancialInstitutionMerchantId id = fiMerchant.getId();
        FinancialInstitutionMerchantId id2 = FinancialInstitutionMerchantId.of(merchant, fi);

        // then
        assertThat(id).isEqualTo(id2);
    }
}
