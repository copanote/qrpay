package com.bccard.qrpay.domain.log;

import com.bccard.qrpay.domain.log.repository.QrpayLogQueryRepository;
import com.bccard.qrpay.domain.log.repository.QrpayLogRepository;
import com.bccard.qrpay.exception.QrpayCustomException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrpayLogService {

    private final QrpayLogRepository qrpayLogCudRepository;
    private final QrpayLogQueryRepository qrpayLogQueryRepository;

    public Long getId() {
        return qrpayLogQueryRepository.getNextSequenceValue();
    }

    @Transactional
    public void saveLog(QrpayLog qrpayLog) {
        qrpayLogCudRepository.save(qrpayLog);
    }

    @Transactional
    public QrpayLog updateLogMessageBody(Long id, String logMessageBody) {
        QrpayLog fetched = qrpayLogQueryRepository
                .findById(id)
                .orElseThrow(() -> new QrpayCustomException(QrpayErrorCode.LOG_NOT_FOUND));

        fetched.getLogMessage().updateBody(logMessageBody);
        return fetched;
    }

    public QrpayLog findById(Long id) {
        return qrpayLogQueryRepository
                .findById(id)
                .orElseThrow(() -> new QrpayCustomException(QrpayErrorCode.LOG_NOT_FOUND));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveApiLog(QrpayLog qrpayLog) {
        qrpayLogCudRepository.save(qrpayLog);
    }

}
