package com.bccard.qrpay.domain.merchant.application.service;

import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.application.port.in.VatUpdateCommand;
import com.bccard.qrpay.domain.merchant.application.port.in.VatUpdateUseCase;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class VatUpdateService implements VatUpdateUseCase {

    private final MerchantQueryRepository merchantQueryRepository;

    @Override
    @Transactional
    public Merchant update(VatUpdateCommand command) {

        Merchant fetchedMerchant = merchantQueryRepository
                .findById(command.merchantId())
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        Long toChangeVatRate = command.enable() ? command.vatRate() : null;
        fetchedMerchant.updateVat(toChangeVatRate);

        return fetchedMerchant;
    }
}
