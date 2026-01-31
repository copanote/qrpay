package com.bccard.qrpay.domain.merchant.application.port.in;


import com.bccard.qrpay.domain.merchant.Merchant;

/**
 * 봉사료변경 UseCase
 *
 */
public interface TipUpdateUseCase {
    Merchant update(TipUpdateCommand command);
}
