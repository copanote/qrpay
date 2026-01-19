package com.bccard.qrpay.domain.device.repository;

import com.bccard.qrpay.domain.device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {}
