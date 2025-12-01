package com.bccard.qrpay.domain.merchant;

import com.bccard.qrpay.domain.common.code.*;
import com.bccard.qrpay.domain.merchant.repository.FinancialInstitutionMerchantRepository;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.domain.merchant.repository.MerchantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Sql("classpath:sql/data.sql")
public class MerchantRepositoryTest {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private MerchantQueryRepository merchantQueryRepository;

    @Autowired
    private FinancialInstitutionMerchantRepository financeInstitutionMerchantRepository;

    @Autowired
    EntityManager em;

    @Test
    void testFind() {
        System.out.println(merchantRepository.findAll().size());
    }

    @Test
    void testQueryDsl() {
        merchantQueryRepository.findAll();
    }

    @Test
    void test_createSequence() {
        // Given
        // When
        // Then
        long seq = merchantQueryRepository.getNextSequenceValue();
        long seq2 = merchantQueryRepository.getNextSequenceValue();

        assertThat(seq).isEqualTo(seq2 - 1);
    }

    @Test
    void test_save_merchant() {
        Merchant merchant = Merchant.createNewMerchant()
                .merchantId("1")
                .merchantStatus(MerchantStatus.ACTIVE)
                .merchantType(MerchantType.BASIC)
                .merchantRegister(MerchantRegister.MERCHANT)
                .mcc("7933")
                .businessNo("12345678")
                .merchantName("test")
                .merchantEnglishName("testEnglish")
                .cityName("Seoul")
                .cityEnglishName("SeoulEnglish")
                .merchantZipCode("12345")
                .merchantTelAreaNo("02")
                .merchantTelMiddleNo("520")
                .merchantTelLastNo("4813")
                .representativeName("ShinDongWook")
                .representativeBirthDay("19991231")
                .representativeEmail("dongwookshin@bccard.com")
                .acquisitionMethod(AcquisitionMethod.EDC)
                .build();

        Merchant saved = merchantRepository.save(merchant);
        merchantRepository.flush();

        FinancialInstitutionMerchant bccardMerchant = FinancialInstitutionMerchant.createNewFinancialInstituteMerchant()
                .merchant(saved)
                .financialInstitution(FinancialInstitution.BCCARD)
                .fiMerchantNo(saved.getBusinessNo())
                .fiMerchantName(saved.getMerchantName())
                .build();

        FinancialInstitutionMerchant lotteMerchant = FinancialInstitutionMerchant.createNewFinancialInstituteMerchant()
                .merchant(saved)
                .financialInstitution(FinancialInstitution.LOTTECARD)
                .fiMerchantNo(saved.getBusinessNo())
                .fiMerchantName(saved.getMerchantName())
                .build();

        financeInstitutionMerchantRepository.save(bccardMerchant);
        financeInstitutionMerchantRepository.save(lotteMerchant);
        financeInstitutionMerchantRepository.flush();


        em.clear();

        Merchant byId = merchantQueryRepository.findById(saved.getId()).get();
        System.out.println(byId);
        System.out.println(byId.getFiMerchants().size());

        em.clear();

        List<Merchant> all = merchantRepository.findAll();
        for (Merchant merchant1 : all) {
            List<FinancialInstitutionMerchant> fiMerchants = merchant1.getFiMerchants();
            for (FinancialInstitutionMerchant fiMerchant : fiMerchants) {
                System.out.println(fiMerchant.getFinancialInstitution());
            }
        }

    }

    @Test
    void test_save_financialMerchant() {
    }

    @Test
    void test_findById() {
        Optional<Merchant> merchant = merchantQueryRepository.findById("900004503");
        System.out.println(merchant.get().getMerchantName());
    }
}
