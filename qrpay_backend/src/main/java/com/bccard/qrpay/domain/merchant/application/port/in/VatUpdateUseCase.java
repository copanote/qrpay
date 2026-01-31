package com.bccard.qrpay.domain.merchant.application.port.in;


import com.bccard.qrpay.domain.merchant.Merchant;

/**
 * 부가세 변경 UseCase
 */
public interface VatUpdateUseCase {
    Merchant update(VatUpdateCommand command);
}
