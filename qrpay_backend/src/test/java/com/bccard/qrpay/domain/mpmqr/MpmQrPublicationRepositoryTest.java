package com.bccard.qrpay.domain.mpmqr;


import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.repository.MemberRepository;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.repository.MerchantRepository;
import com.bccard.qrpay.domain.mpmqr.repository.MpmQrPublicationQueryRepository;
import com.bccard.qrpay.domain.mpmqr.repository.MpmQrPublicationRepository;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
public class MpmQrPublicationRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MpmQrPublicationRepository mpmQrPublicationRepository;
    @Autowired
    MpmQrPublicationQueryRepository mpmQrPublicationQueryRepository;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MerchantRepository merchantRepository;

    @Test
    void test_createqrRefId() {
        Long nextSequenceValue = mpmQrPublicationQueryRepository.getNextSequenceValue();
        System.out.println(nextSequenceValue);
    }

    @Test
    void test_save() {

        Merchant merchant = Merchant.createNewMerchant()
                .merchantId("m999")
                .build();


        Member member = Member.createNormalMember()
                .memberId("999")
                .merchant(merchant)
                .loginId("test123")
                .hashedPassword("123")
                .build();

        MpmQrPublication newMpmQr = MpmQrPublication
                .createMpmqrPublication()
                .qrReferenceId("qrref1")
                .merchant(merchant)
                .member(member)
                .pim(PointOfInitMethod.STATIC)
                .amount(1000L)
                .qrData("qrdata")
                .startedAt(MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC))
                .affiliateId("")
                .affiliateRequestValue("")
                .build();

        merchantRepository.save(merchant);
        memberRepository.save(member);
        mpmQrPublicationRepository.save(newMpmQr);
        em.flush();
        em.clear();

        Optional<MpmQrPublication> qrref1 = mpmQrPublicationQueryRepository.findById("qrref1");

        Optional<MpmQrPublication> qrref2 = mpmQrPublicationQueryRepository.findById("qrref2");

        System.out.println(qrref1.get().getQrReferenceId());
        System.out.println(qrref2.isPresent());

    }


}
