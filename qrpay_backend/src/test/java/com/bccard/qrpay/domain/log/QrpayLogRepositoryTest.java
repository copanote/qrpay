package com.bccard.qrpay.domain.log;

import com.bccard.qrpay.domain.log.repository.QrpayLogQueryRepository;
import com.bccard.qrpay.domain.log.repository.QrpayLogRepository;
import com.bccard.qrpay.external.nice.NiceSmsRequestor;
import com.bccard.qrpay.external.nice.NiceSmsState;
import com.bccard.qrpay.external.nice.dto.NiceSmsSessionData;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class QrpayLogRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    QrpayLogRepository qrpayLogRepository;

    @Autowired
    QrpayLogQueryRepository qrpayLogQueryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test_save() throws JsonProcessingException {

        Long id = qrpayLogQueryRepository.getNextSequenceValue();

        String uuid = UUID.randomUUID().toString();
        NiceSmsSessionData smsSessionData = NiceSmsSessionData.builder()
                .referenceId(uuid)
                .createdAt(MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC))
                .niceSmsRequestor(NiceSmsRequestor.PASSWORD_RESET)
                .state(NiceSmsState.REQUEST_PROGRESS)
                .build();
        QrpayLog qrpayLog = QrpayLog.smsNice(id, objectMapper.writeValueAsString(smsSessionData));

        qrpayLogRepository.save(qrpayLog);
        em.flush();
    }
}
