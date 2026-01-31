package com.bccard.qrpay.domain.qrkit;

import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.MerchantStatus;
import com.bccard.qrpay.domain.common.code.QrKitApplicationChannel;
import com.bccard.qrpay.domain.common.code.QrKitShippingStatus;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.MerchantService;
import com.bccard.qrpay.domain.merchant.application.port.in.ChangeMerchantNameCommand;
import com.bccard.qrpay.domain.merchant.application.port.in.ChangeMerchantNameUseCase;
import com.bccard.qrpay.domain.mpmqr.EmvMpmQrService;
import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;
import com.bccard.qrpay.domain.mpmqr.dto.PublishBcEmvMpmQrReqDto;
import com.bccard.qrpay.domain.qrkit.dto.MpmQrKitApplyReqDto;
import com.bccard.qrpay.domain.qrkit.repository.MpmQrKitQueryRepository;
import com.bccard.qrpay.domain.qrkit.repository.MpmQrKitRepository;
import com.bccard.qrpay.exception.MemberException;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class QrKitService {
    private final MpmQrKitQueryRepository mpmQrKitQueryRepository;
    private final MpmQrKitRepository mpmQrKitCUDRepository;
    private final MerchantService merchantService;
    private final EmvMpmQrService emvMpmQrService;
    private final ChangeMerchantNameUseCase changeMerchantNameUseCase;

    public MpmQrKitApplication apply(MpmQrKitApplyReqDto reqDto) {

        Member member = reqDto.getMember();
        if (member.getRole() != MemberRole.MASTER) {
            throw new MemberException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Merchant merchant = reqDto.getMerchant();
        if (merchant.getMerchantStatus() == MerchantStatus.CANCELLED) {
            throw new MerchantException(QrpayErrorCode.MERCHANT_CANCELED);
        }

        List<MpmQrKitApplication> mpmQrKitApplications = this.find(member);
        if (mpmQrKitApplications.size() >= 3) {
            throw new MerchantException(QrpayErrorCode.QRKIT_MAX_LIMIT_EXCEEDED);
        }

        if (!reqDto.getReqMerchantName().equals(merchant.getMerchantName())) {

            ChangeMerchantNameCommand command = ChangeMerchantNameCommand.builder()
                    .merchantId(member.getMerchant().getMerchantId())
                    .toUpdateName(reqDto.getReqMerchantName())
                    .build();
            merchant = changeMerchantNameUseCase.change(command);
        }

        PublishBcEmvMpmQrReqDto emvmpmQrReq =
                PublishBcEmvMpmQrReqDto.staticEmvMpm(member.getMemberId(), merchant, "410");
        MpmQrPublication mpmQrPublication = emvMpmQrService.publishBcEmvMpmQr(emvmpmQrReq);

        Long id = mpmQrKitQueryRepository.getNextSequenceValue();
        MpmQrKitApplication newQrKit = MpmQrKitApplication.newQrKit()
                .id(id)
                .merchantId(merchant.getMerchantId())
                .merchantName(merchant.getMerchantName())
                .address1(reqDto.getAddress1())
                .address2(reqDto.getAddress2())
                .zipCode(reqDto.getZip())
                .phoneNo(reqDto.getPhoneNo())
                .qrReferenceId(mpmQrPublication.getQrReferenceId())
                .extraApplication(true)
                .status(QrKitShippingStatus.APPLIED)
                .applicationChannel(QrKitApplicationChannel.QRPAY_APP)
                .build();

        return mpmQrKitCUDRepository.save(newQrKit);
    }

    public List<MpmQrKitApplication> find(Member member) {

        if (member.getRole() != MemberRole.MASTER) {
            throw new MemberException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Merchant merchant = member.getMerchant();
        if (merchant.getMerchantStatus() == MerchantStatus.CANCELLED) {
            throw new MerchantException(QrpayErrorCode.MERCHANT_CANCELED);
        }

        return mpmQrKitQueryRepository.findByMerchantId(member.getMerchant().getMerchantId());
    }
}
