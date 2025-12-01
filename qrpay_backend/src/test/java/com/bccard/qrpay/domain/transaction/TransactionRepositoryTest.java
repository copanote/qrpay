package com.bccard.qrpay.domain.transaction;

import com.bccard.qrpay.domain.common.code.AuthorizeType;
import com.bccard.qrpay.domain.common.code.PaymentStatus;
import com.bccard.qrpay.domain.common.code.ServiceType;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.repository.MerchantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
public class TransactionRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TransactionQueryRepository transactionQueryRepository;
    @Autowired
    MerchantRepository merchantRepository;

    @Test
    void test_save() {

        Merchant merchant = Merchant.createNewMerchant()
                .merchantId("m999")
                .build();

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

        Optional<Transaction> byId = transactionQueryRepository.findById(
                TransactionId.create()
                        .transactionId("t1")
                        .affiliateTransactionId("at1")
                        .authorizeType(AuthorizeType.AUTHORIZE).build()
        );

        System.out.println(byId.isPresent());
        System.out.println(byId.get().getTransactionId());

    }

}
