package com.bccard.qrpay.domain.member;

import com.bccard.qrpay.domain.common.code.*;
import com.bccard.qrpay.domain.member.repository.MemberQueryRepository;
import com.bccard.qrpay.domain.member.repository.MemberRepository;
import com.bccard.qrpay.domain.merchant.FinancialInstitutionMerchant;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.repository.FinancialInstitutionMerchantRepository;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.domain.merchant.repository.MerchantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class MemberRepositoryTest {


    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberQueryRepository memberQueryRepository;


    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private MerchantQueryRepository merchantQueryRepository;

    @Autowired
    private FinancialInstitutionMerchantRepository financeInstitutionMerchantRepository;

    @BeforeEach
    void beforeEach() {
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
    }


    @Test
    void test_createSequence() {
        long seq = memberQueryRepository.getNextSequenceValue();
        long seq2 = memberQueryRepository.getNextSequenceValue();

        assertThat(seq).isEqualTo(seq2 - 1);
    }

    @Test
    void test_save() {

        Merchant merchant = Merchant.createNewMerchant()
                .merchantId("999")
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

        FinancialInstitutionMerchant bccardMerchant = FinancialInstitutionMerchant.createNewFinancialInstituteMerchant()
                .merchant(saved)
                .financialInstitution(FinancialInstitution.BCCARD)
                .fiMerchantNo(saved.getBusinessNo())
                .fiMerchantName(saved.getMerchantName())
                .build();
        financeInstitutionMerchantRepository.save(bccardMerchant);


//        Merchant merchant = merchantQueryRepository.findById("1");


        long seq = memberQueryRepository.getNextSequenceValue();

        Member newMember = Member.createMasterMemeber()
                .memberId(String.valueOf(seq))
                .loginId("test01")
                .hashedPassword("enc")
                .email("abc@gmail.com")
                .termsAgreeInfo("1.3")
                .referrerId("")
                .merchant(saved)
                .build();

        memberRepository.save(newMember);
        em.flush();
        em.clear();

        System.out.println(newMember.getMerchant().getMerchantName());


        Optional<Member> selectedMember = memberQueryRepository.findById(newMember.getMemberId());

        System.out.println(selectedMember.get().getMerchant().getMerchantName());

        System.out.println(selectedMember.get().getMerchant().getFiMerchants().get(0).getFiMerchantName());


    }


}
