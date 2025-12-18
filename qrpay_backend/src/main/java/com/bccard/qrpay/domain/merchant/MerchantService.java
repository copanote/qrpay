package com.bccard.qrpay.domain.merchant;

import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.domain.merchant.repository.MerchantRepository;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantService {
    private final MerchantQueryRepository merchantQueryRepository;
    private final MerchantRepository merchantRepository;


    public Optional<Merchant> findById(String merchantId) {
        return merchantQueryRepository.findById(merchantId);
    }

    @Transactional
    public Merchant updateMerchantName(Merchant merchant, String updatedMerchantName) {

        Merchant fetchedMerchant = merchantQueryRepository.findById(merchant.getMerchantId()).orElseThrow(
                () -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND)
        );

        if (!fetchedMerchant.getMerchantName().equals(updatedMerchantName)) {
            fetchedMerchant.updateMerchantName(updatedMerchantName);
        }

        return fetchedMerchant;
    }

    @Transactional
    public Merchant updateVat(Merchant merchant, BigDecimal updatedVat) {

        Merchant fetchedMerchant = merchantQueryRepository.findById(merchant.getMerchantId()).orElseThrow(
                () -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND)
        );

        fetchedMerchant.updateVat(updatedVat);
        return fetchedMerchant;
    }

    @Transactional
    public Merchant updateTip(Merchant merchant, BigDecimal tip) {

        Merchant fetchedMerchant = merchantQueryRepository.findById(merchant.getMerchantId()).orElseThrow(
                () -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND)
        );

        fetchedMerchant.updateTip(tip);
        return fetchedMerchant;
    }


}
