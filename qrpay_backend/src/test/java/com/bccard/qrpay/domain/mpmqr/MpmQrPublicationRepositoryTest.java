package com.bccard.qrpay.domain.mpmqr;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.repository.MemberRepository;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.domain.merchant.repository.MerchantRepository;
import com.bccard.qrpay.domain.mpmqr.dto.PublishBcEmvMpmQrReqDto;
import com.bccard.qrpay.domain.mpmqr.repository.MpmQrPublicationQueryRepository;
import com.bccard.qrpay.domain.mpmqr.repository.MpmQrPublicationRepository;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Sql("classpath:sql/data.sql")
@ActiveProfiles("test")
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

    @Autowired
    private MerchantQueryRepository merchantQueryRepository;

    @Autowired
    EmvMpmQrService emvMpmQrService;

    @Test
    void test_createqrRefId() {
        String nextSequenceValue = mpmQrPublicationQueryRepository.createQrReferenceId();
        System.out.println(nextSequenceValue);
    }

    @Test
    void test_save() {

        Merchant merchant = Merchant.createNewMerchant().merchantId("m999").build();

        Member member = Member.createNewEmployee("999", merchant, "test123", "encpw", true);

        MpmQrPublication newMpmQr = MpmQrPublication.createMpmqrPublication()
                .qrReferenceId("qrref1")
                .merchant(merchant)
                .memberId("999")
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

    @Test
    void test_findStaticmpmQR() {
        Merchant merchant = Merchant.createNewMerchant().merchantId("m999").build();
        mpmQrPublicationQueryRepository.findNewestStaticMpmQr(merchant);
    }

    @Test
    @Rollback(value = false)
    void test_publishEmvMpm() {

        String memberId = "TEST11";

        Optional<Merchant> merchant = merchantQueryRepository.findById("900004862");

        PublishBcEmvMpmQrReqDto req = PublishBcEmvMpmQrReqDto.staticEmvMpm(memberId, merchant.get(), "410");

        MpmQrPublication mpmQrPublication = emvMpmQrService.publishBcEmvMpmQr(req);
        System.out.println(mpmQrPublication.getQrReferenceId());
        System.out.println(mpmQrPublication.getQrData());
    }
}
