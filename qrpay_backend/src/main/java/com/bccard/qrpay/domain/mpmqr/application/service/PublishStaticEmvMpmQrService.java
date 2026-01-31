package com.bccard.qrpay.domain.mpmqr.application.service;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.domain.mpmqr.BcEmvMpmQrCodeSpec;
import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;
import com.bccard.qrpay.domain.mpmqr.application.port.in.PublishStaticEmvMpmQrCommand;
import com.bccard.qrpay.domain.mpmqr.application.port.in.PublishStaticEmvMpmQrUseCase;
import com.bccard.qrpay.domain.mpmqr.repository.MpmQrPublicationQueryRepository;
import com.bccard.qrpay.domain.mpmqr.repository.MpmQrPublicationRepository;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PublishStaticEmvMpmQrService implements PublishStaticEmvMpmQrUseCase {

    private final MpmQrPublicationRepository mpmQrPublicationCudRepository;
    private final MpmQrPublicationQueryRepository mpmQrPublicationQueryRepository;
    private final MerchantQueryRepository merchantQueryRepository;

    @Override
    @Transactional
    public MpmQrPublication publishStatic(PublishStaticEmvMpmQrCommand command) {

        log.info("publishDynamicBcEmvMpmQr={}", command.toString());

        String qrReferenceId = mpmQrPublicationQueryRepository.createQrReferenceId();
        Merchant merchant = merchantQueryRepository
                .findById(command.merchantId())
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        String qrCodeData =
                BcEmvMpmQrCodeSpec.staticCodeData(command.memberId(), merchant, qrReferenceId, command.currency());

        MpmQrPublication mpmQrPublication = MpmQrPublication.createMpmqrPublication()
                .qrReferenceId(qrReferenceId)
                .merchant(merchant)
                .memberId(command.memberId())
                .pim(PointOfInitMethod.STATIC)
                .qrData(qrCodeData)
                .affiliateId(command.affiliateId())
                .affiliateRequestValue(command.affiliateRequestValue())
                .startedAt(command.startedAt())
                .build();

        return mpmQrPublicationCudRepository.save(mpmQrPublication);
    }
}
