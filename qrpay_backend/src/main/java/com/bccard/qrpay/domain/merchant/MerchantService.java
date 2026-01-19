package com.bccard.qrpay.domain.merchant;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.domain.merchant.repository.MerchantRepository;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantService {
    private final MerchantQueryRepository merchantQueryRepository;
    private final MerchantRepository merchantRepository;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public Optional<Merchant> findById(String merchantId) {
        return merchantQueryRepository.findById(merchantId);
    }

    @Transactional(readOnly = true)
    public Optional<Merchant> findByBcMerchantNo(String bcMerchantNo) {
        return merchantQueryRepository.findByFinancialInstitutionAndFiMerchantNo(
                FinancialInstitution.BCCARD, bcMerchantNo);
    }

    @Transactional
    public Merchant updateMerchantName(Merchant merchant, String updatedMerchantName) {

        Merchant fetchedMerchant = merchantQueryRepository
                .findById(merchant.getMerchantId())
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        if (updatedMerchantName.length() > 14) {
            throw new MerchantException(QrpayErrorCode.MERCHANT_NAME_LENGTH_POLICY_VIOLATION);
        }

        if (!fetchedMerchant.getMerchantName().equals(updatedMerchantName)) {
            fetchedMerchant.updateMerchantName(updatedMerchantName);
        }

        return fetchedMerchant;
    }

    @Transactional
    public Merchant updateVat(Merchant merchant, BigDecimal updatedVat) {

        Merchant fetchedMerchant = merchantQueryRepository
                .findById(merchant.getMerchantId())
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        fetchedMerchant.updateVat(updatedVat);
        return fetchedMerchant;
    }

    @Transactional
    public Merchant updateTip(Merchant merchant, BigDecimal tip) {

        Merchant fetchedMerchant = merchantQueryRepository
                .findById(merchant.getMerchantId())
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        fetchedMerchant.updateTip(tip);
        return fetchedMerchant;
    }

    @Transactional
    public Merchant cancel(Merchant merchant) {

        // TODO:: 카드사연결해지부분
        Merchant fetchedMerchant = merchantQueryRepository
                .findById(merchant.getMerchantId())
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));
        int count = memberService.cancelAll(merchant);
        log.info("canceled Member={}", count);
        merchant.cancel();

        return fetchedMerchant;
    }
}
