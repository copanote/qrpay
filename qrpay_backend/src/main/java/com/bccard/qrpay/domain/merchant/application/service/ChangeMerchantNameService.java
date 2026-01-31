package com.bccard.qrpay.domain.merchant.application.service;

import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.application.port.in.ChangeMerchantNameCommand;
import com.bccard.qrpay.domain.merchant.application.port.in.ChangeMerchantNameUseCase;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChangeMerchantNameService implements ChangeMerchantNameUseCase {

    private final MerchantQueryRepository merchantQueryRepository;

    @Override
    @Transactional
    public Merchant change(ChangeMerchantNameCommand command) {

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
