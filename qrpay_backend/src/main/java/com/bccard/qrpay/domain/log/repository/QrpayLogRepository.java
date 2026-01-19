package com.bccard.qrpay.domain.log.repository;

import com.bccard.qrpay.domain.log.QrpayLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrpayLogRepository extends JpaRepository<QrpayLog, Long> {}
