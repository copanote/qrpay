package com.bccard.qrpay.domain.mpmqr.application.service;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.domain.mpmqr.BcEmvMpmQrCodeSpec;
import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;
import com.bccard.qrpay.domain.mpmqr.application.port.in.PublishDynamicEmvMpmQrCommand;
import com.bccard.qrpay.domain.mpmqr.application.port.in.PublishDynamicEmvMpmQrUseCase;
import com.bccard.qrpay.domain.mpmqr.repository.MpmQrPublicationQueryRepository;
import com.bccard.qrpay.domain.mpmqr.repository.MpmQrPublicationRepository;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PublishDynamicEmvMpmQrService implements PublishDynamicEmvMpmQrUseCase {

    private final MpmQrPublicationRepository mpmQrPublicationCudRepository;
    private final MpmQrPublicationQueryRepository mpmQrPublicationQueryRepository;
    private final MerchantQueryRepository merchantQueryRepository;

    @Override
    public MpmQrPublication publishDynamic(PublishDynamicEmvMpmQrCommand command) {
        log.info("publishDynamicBcEmvMpmQr={}", command.toString());

        String qrReferenceId = mpmQrPublicationQueryRepository.createQrReferenceId();
        Merchant merchant = merchantQueryRepository
                .findById(command.merchantId())
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        String qrCodeData = BcEmvMpmQrCodeSpec.dynamicCodeData(
                command.memberId(),
                merchant,
                qrReferenceId,
                command.currency(),
                command.amount(),
                command.installment());

        MpmQrPublication mpmQrPublication = MpmQrPublication.createMpmqrPublication()
                .qrReferenceId(qrReferenceId)
                .merchant(merchant)
                .memberId(command.memberId())
                .pim(PointOfInitMethod.DYNAMIC)
                .amount(command.amount())
                .qrData(qrCodeData)
                .affiliateId(command.affiliateId())
                .affiliateRequestValue(command.affiliateRequestValue())
                .startedAt(command.startedAt())
                .build();
        return mpmQrPublicationCudRepository.save(mpmQrPublication);
    }
}
