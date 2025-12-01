package com.bccard.qrpay.domain.log;

import com.bccard.qrpay.domain.log.repository.QrpayLogQueryRepository;
import com.bccard.qrpay.domain.log.repository.QrpayLogRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class QrpayLogRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    QrpayLogRepository qrpayLogRepository;

    @Autowired
    QrpayLogQueryRepository qrpayLogQueryRepository;


    @Test
    void test_save() {
        qrpayLogRepository.save(QrpayLog.builder().build());
//        qrpayLogRepository.save(QrpayLog.builder().build());
//        qrpayLogRepository.save(QrpayLog.builder().build());
//        qrpayLogRepository.save(QrpayLog.builder().build());
//        qrpayLogRepository.save(QrpayLog.builder().build());
//        qrpayLogRepository.save(QrpayLog.builder().build());

        em.flush();
    }

}
