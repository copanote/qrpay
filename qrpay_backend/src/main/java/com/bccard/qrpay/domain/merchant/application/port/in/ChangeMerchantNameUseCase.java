package com.bccard.qrpay.domain.merchant.application.port.in;

import com.bccard.qrpay.domain.merchant.Merchant;

/**
 *  가맹점이름변경 UseCase
 */
public interface ChangeMerchantNameUseCase {
    Merchant change(ChangeMerchantNameCommand command);
}
