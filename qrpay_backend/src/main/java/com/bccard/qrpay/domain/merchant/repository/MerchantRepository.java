package com.bccard.qrpay.domain.merchant.repository;

import com.bccard.qrpay.domain.merchant.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, String> {}
