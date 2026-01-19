package com.bccard.qrpay.domain.transaction;

import com.bccard.qrpay.domain.common.code.MerchantStatus;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.MerchantService;
import com.bccard.qrpay.domain.transaction.dto.MonthlySalesDto;
import com.bccard.qrpay.domain.transaction.dto.TransHistoryResponse;
import com.bccard.qrpay.domain.transaction.dto.TransSearchCondition;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.QrpayCustomException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransHistoryQueryRepository transHistoryQueryRepository;
    private final MerchantService merchantService;

    @Transactional(readOnly = true)
    public List<MonthlySalesDto> fetchMonthlySalesList(String merchantId, int period) {

        Merchant merchant = merchantService
                .findById(merchantId)
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        if (MerchantStatus.ACTIVE != merchant.getMerchantStatus()) {
            throw new MerchantException(QrpayErrorCode.MERCHANT_IS_NOT_ACTIVE);
        }

        return transHistoryQueryRepository.fetchMonthlySalesList(merchant, 3);
    }

    @Transactional(readOnly = true)
    public Slice<TransHistoryResponse> getTransactionHistory(TransSearchCondition searchCondition, Pageable pageable) {

        if (!searchCondition.isValidPeriod()) {
            throw new QrpayCustomException(QrpayErrorCode.INVALID_SEARCH_CONDITION);
        }

        return transHistoryQueryRepository.searchSlice(searchCondition, pageable);
    }
}
