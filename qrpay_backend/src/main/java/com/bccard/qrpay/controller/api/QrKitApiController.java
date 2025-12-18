package com.bccard.qrpay.controller.api;

import com.bccard.qrpay.config.web.argumentresolver.LoginMember;
import com.bccard.qrpay.controller.api.common.QrpayApiResponse;
import com.bccard.qrpay.controller.api.dtos.ApiQrKitApplyReqDto;
import com.bccard.qrpay.controller.api.dtos.QrKitApplicationDto;
import com.bccard.qrpay.controller.api.dtos.QrKitApplicationResDto;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.MerchantService;
import com.bccard.qrpay.domain.mpmqr.EmvMpmQrService;
import com.bccard.qrpay.domain.qrkit.MpmQrKitApplication;
import com.bccard.qrpay.domain.qrkit.QrKitService;
import com.bccard.qrpay.domain.qrkit.dto.MpmQrKitApplyReqDto;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/qrpay/api")
public class QrKitApiController {

    private final MerchantService merchantService;
    private final QrKitService qrKitService;
    private final EmvMpmQrService emvMpmQrService;


    @PostMapping(value = "/v1/merchant/{merchantId}/qrkit/apply")
    @ResponseBody
    public ResponseEntity<?> qrkitApply(
            @LoginMember Member member,
            @PathVariable("merchantId") String merchantId,
            @RequestBody ApiQrKitApplyReqDto reqDto
    ) throws Exception {

        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Merchant merchant = member.getMerchant();
        if (!merchant.getMerchantId().equals(merchantId)) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        MpmQrKitApplyReqDto serviceQrKitReqDto = MpmQrKitApplyReqDto.builder()
                .member(member)
                .merchant(merchant)
                .reqMerchantName(reqDto.getMerchantName())
                .address1(reqDto.getAddress1())
                .address2(reqDto.getAddress2())
                .zip(reqDto.getZip())
                .phoneNo(reqDto.getPhoneNo())
                .build();

        //TODO 3개까지 신청 가능??
        MpmQrKitApplication newQrKit = qrKitService.apply(serviceQrKitReqDto);
        QrKitApplicationDto from = QrKitApplicationDto.from(newQrKit);

        QrpayApiResponse<QrKitApplicationDto> res = QrpayApiResponse.ok(from);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping(value = "/v1/merchant/{merchantId}/qrkit/applications")
    @ResponseBody
    public ResponseEntity<?> qrkitHistory(
            @LoginMember Member member,
            @PathVariable("merchantId") String merchantId) throws Exception {

        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Merchant merchant = member.getMerchant();
        if (!merchant.getMerchantId().equals(merchantId)) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        List<MpmQrKitApplication> mpmQrKitApplications = qrKitService.find(member);
        List<QrKitApplicationDto> list = mpmQrKitApplications.stream()
                .map(QrKitApplicationDto::from).toList();

        QrpayApiResponse<QrKitApplicationResDto> res = QrpayApiResponse.ok(QrKitApplicationResDto.of(list));

        return ResponseEntity.ok().body(res);
    }

}
