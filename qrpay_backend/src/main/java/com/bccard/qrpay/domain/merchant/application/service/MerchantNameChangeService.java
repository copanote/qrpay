package com.bccard.qrpay.domain.merchant.application.service;

import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.application.port.in.MerchantNameChangeCommand;
import com.bccard.qrpay.domain.merchant.application.port.in.MerchantNameChangeUseCase;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantNameChangeService implements MerchantNameChangeUseCase {

    private final MerchantQueryRepository merchantQueryRepository;

    @Override
    @Transactional
    public Merchant change(MerchantNameChangeCommand command) {

        Objects.requireNonNull(command, "UpdateMerchantNameCommand cannot be null");

        if (command.toUpdateName().length() > Merchant.MAX_NAME_LENGTH) {
            throw new MerchantException(QrpayErrorCode.MERCHANT_NAME_LENGTH_POLICY_VIOLATION);
        }

        Merchant fetchedMerchant = merchantQueryRepository
                .findById(command.merchantId())
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        if (!fetchedMerchant.getMerchantName().equals(command.toUpdateName())) {
            // Dirty Checking Update!
            fetchedMerchant.updateMerchantName(command.toUpdateName());
        }

        return fetchedMerchant;
    }
}
