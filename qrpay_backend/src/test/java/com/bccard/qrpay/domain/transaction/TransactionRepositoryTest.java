package com.bccard.qrpay.domain.transaction;

import com.bccard.qrpay.domain.common.code.AuthorizeType;
import com.bccard.qrpay.domain.common.code.PaymentStatus;
import com.bccard.qrpay.domain.common.code.ServiceType;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.domain.merchant.repository.MerchantRepository;
import com.bccard.qrpay.domain.transaction.dto.MonthlySalesDto;
import com.bccard.qrpay.domain.transaction.dto.TransHistoryResponse;
import com.bccard.qrpay.domain.transaction.dto.TransSearchCondition;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Sql("classpath:sql/data.sql")
public class TransactionRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionQueryRepository transactionQueryRepository;

    @Autowired
    TransHistoryQueryRepository transHistoryQueryRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    MerchantQueryRepository merchantQueryRepository;

    @Test
    void test_save() {

        Merchant merchant = Merchant.createNewMerchant().merchantId("m999").build();

        merchantRepository.save(merchant);

        Transaction t = Transaction.builder()
                .transactionId("t1")
                .affiliateTransactionId("at1")
                .authorizeType(AuthorizeType.AUTHORIZE)
                .serviceType(ServiceType.BC)
                .paymentStatus(PaymentStatus.APPROVED)
                .merchant(merchant)
                .build();

        transactionRepository.save(t);
        em.flush();
        em.clear();

        Optional<Transaction> byId = transactionQueryRepository.findById(TransactionId.create()
                .transactionId("t1")
                .affiliateTransactionId("at1")
                .authorizeType(AuthorizeType.AUTHORIZE)
                .build());

        System.out.println(byId.isPresent());
        System.out.println(byId.get().getTransactionId());
    }

    @Test
    void test_select() {

        String testId = "237040525228";
        List<Transaction> byTrnsId = transactionQueryRepository.findByTrnsId(testId);
        System.out.println(byTrnsId);
    }

    @Test
    void test_mothnly() {
        Merchant m = merchantQueryRepository.findById("900004541").orElseThrow();
        List<MonthlySalesDto> monthlySalesDtos = transHistoryQueryRepository.fetchMonthlySalesList(m, 3);
        System.out.println(monthlySalesDtos);
    }

    @Test
    void test_slice() {
        Merchant m = merchantQueryRepository.findById("900004541").orElseThrow();

        TransSearchCondition condition = TransSearchCondition.builder()
                .merchant(m)
                .startYmd("20251101")
                .endYmd("20260101")
                .authNoLast4("0409")
                //                .paymentStatus()
                //                .serviceT
                .build();

        Pageable p = PageRequest.of(0, 10);

        Slice<TransHistoryResponse> transactions = transHistoryQueryRepository.searchSlice(condition, p);
        System.out.println(transactions.getContent());
    }
}
