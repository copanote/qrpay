package com.bccard.qrpay.domain.qrkit.repository;

import com.bccard.qrpay.domain.qrkit.MpmQrKitApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MpmQrKitRepository extends JpaRepository<MpmQrKitApplication, Long> {
}
