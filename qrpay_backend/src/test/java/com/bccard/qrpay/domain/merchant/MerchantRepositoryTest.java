package com.bccard.qrpay.domain.merchant;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;
import com.bccard.qrpay.domain.merchant.fixture.MerchantFixture;
import com.bccard.qrpay.domain.merchant.repository.FinancialInstitutionMerchantRepository;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.domain.merchant.repository.MerchantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MerchantQueryRepository.class}) //QueryDSL Repository 추가
@Sql(scripts = {"classpath:sql/data.sql"})
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
    @DisplayName("[성공] 시퀀스생성")
    void test_createSequence() {
        // Give When Then
        long seq = merchantQueryRepository.getNextSequenceValue();
        long seq2 = merchantQueryRepository.getNextSequenceValue();
        assertThat(seq).isEqualTo(seq2 - 1);
    }

    @Test
    @DisplayName("[성공] 가맹점 저장 AndThen 조회")
    void test_save_merchant() {
        //Given
        Merchant merchant = MerchantFixture.createMerchant();
        FinancialInstitutionMerchant fiMerchant = MerchantFixture.createFiMerchant(merchant);
        merchant.addFinancialInstitute(fiMerchant);

        Merchant saved = merchantRepository.save(merchant);
        FinancialInstitutionMerchant saved1 = financeInstitutionMerchantRepository.save(fiMerchant);
        merchantRepository.flush();
        financeInstitutionMerchantRepository.flush();

        em.clear();

        //When
        Merchant foundMerchant = merchantQueryRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("가맹점 조회 실패"));

        //Then
        assertThat(foundMerchant.getMerchantId()).isEqualTo(saved.getMerchantId());
        assertThat(foundMerchant.getFiMerchants()).hasSize(1);
    }

    @Test
    @DisplayName("[성공] merchantQueryRepository: findById")
    void test_findById() {

        //Given
        //361 366 가맹점 두개 저장되어 있음
        String testMerchantId = "900004862";

        //When
        Merchant foundMerchant = merchantQueryRepository.findById(testMerchantId)
                .orElseThrow(() -> new RuntimeException("가맹점 조회 실패"));
        //Then
        assertThat(foundMerchant.getMerchantId()).isEqualTo(testMerchantId);
        assertThat(foundMerchant.getFiMerchants()).hasSize(2);
    }

    @Test
    @DisplayName("[성공] merchantQueryRepository: findByFinancialInstitutionAndFiMerchantNo")
    void test_findByFinancialInstitutionAndFiMerchantNo() {

        //Given
        // values ('900004862','361','791722496','90000981','20251015134547',null,null,'이아철판볶음');
        // values ('900004862','366','0081239709','QRPAY','20251015134553',null,null,'이아철판볶음');
        String fiMerchantNo = "791722496";
        FinancialInstitution fi = FinancialInstitution.BCCARD;

        String mpmMerchantId = "900004862";


        //When
        Merchant foundMerchant = merchantQueryRepository
                .findByFinancialInstitutionAndFiMerchantNo(fi, fiMerchantNo)
                .orElseThrow(() -> new RuntimeException("가맹점 조회 실패"));
        //Then
        assertThat(foundMerchant.getMerchantId()).isEqualTo(mpmMerchantId);
        assertThat(foundMerchant.getFiMerchants().getFirst().getFiMerchantNo()).isEqualTo(fiMerchantNo);
    }

}
