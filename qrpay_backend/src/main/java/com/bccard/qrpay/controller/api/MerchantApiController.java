package com.bccard.qrpay.controller.api;


import com.bccard.qrpay.auth.domain.CustomUserDetails;
import com.bccard.qrpay.controller.api.dtos.*;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.MerchantService;
import com.bccard.qrpay.domain.mpmqr.EmvMpmService;
import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;
import com.bccard.qrpay.domain.mpmqr.dto.PublishBcEmvMpmQrReqDto;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.code.AuthErrorCode;
import com.bccard.qrpay.utils.ZxingQrcode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/qrpay/api")
public class MerchantApiController {

    private final MerchantService merchantService;
    private final MemberService memberService;
    private final EmvMpmService emvMpmService;


    @RequestMapping(value = "/v1/merchant/info")
    @ResponseBody
    public void merchantInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(AuthErrorCode.INVALID_AUTHORIZATION);
        }
    }


    @PostMapping(value = "/v1/merchant/change-vat")
    @ResponseBody
    public ResponseEntity<?> changeVat(VatChangeReqDto reqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(AuthErrorCode.INVALID_AUTHORIZATION);
        }

        BigDecimal updateVat = null;
        if (reqDto.isEnableVat()) {
            updateVat = BigDecimal.valueOf(reqDto.getVatRate());
        }
        Merchant changed = merchantService.updateVat(member.getMerchant(), updateVat);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/v1/merchant/change-tip")
    public ResponseEntity<?> changeTip(TipChangeReqDto reqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(AuthErrorCode.INVALID_AUTHORIZATION);
        }

        BigDecimal updateVat = null;
        if (reqDto.isEnableTip()) {
            updateVat = BigDecimal.valueOf(reqDto.getTipRate());
        }

        Merchant changed = merchantService.updateTip(member.getMerchant(), updateVat);
        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/v1/merchant/change-name")
    @ResponseBody
    public ResponseEntity<?> changeName(@RequestBody @Validated MerchantNameChangeReqDto req) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(AuthErrorCode.INVALID_AUTHORIZATION);
        }

        Merchant merchant = merchantService.updateMerchantName(member.getMerchant(), req.getName());
        PublishBcEmvMpmQrReqDto reqEmvMpm = PublishBcEmvMpmQrReqDto.staticEmvMpm(
                member.getMemberId(),
                merchant,
                "410"
        );

        MpmQrPublication staticMpmQr = emvMpmService.publishBcEmvMpmQr(reqEmvMpm);
        MpmQrInfoResDto out = MpmQrInfoResDto.staticMpmQrInfo(merchant.getMerchantName(), ZxingQrcode.base64EncodedQrImageForQrpay(staticMpmQr.getQrData()));

        return ResponseEntity.ok().body(out);
    }

    @PostMapping(value = "/v1/merchant/mpmqr")
    @ResponseBody
    public ResponseEntity<?> mpmqr(@RequestBody @Validated MpmQrInfoReqDto req) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        Merchant merchant = member.getMerchant();

        MpmQrPublication emvmpmQr;
        MpmQrInfoResDto out = null;

        log.info("MpmQrInfoReqDto={}", req.toString());
        if (req.getPim() == PointOfInitMethod.DYNAMIC) {

            if (req.getAmount() == null || req.getInstallment() == null) {
                throw new IllegalArgumentException("amout or installment should not be null");
            }

            PublishBcEmvMpmQrReqDto publishEmvMpmReqDto = PublishBcEmvMpmQrReqDto.dynamicEmvMpm(
                    member.getMemberId(),
                    merchant,
                    req.getAmount(),
                    req.getInstallment(),
                    "410"
            );

            log.info("Publish Dynamic emvmpm qr={}", publishEmvMpmReqDto);

            emvmpmQr = emvMpmService.publishBcEmvMpmQr(publishEmvMpmReqDto);
            out = MpmQrInfoResDto.dynamicMpmQrInfo(
                    merchant.getMerchantName(),
                    ZxingQrcode.base64EncodedQrImageForQrpay(emvmpmQr.getQrData()),
                    emvmpmQr.getAmount(),
                    req.getInstallment()
            );

        } else if (req.getPim() == PointOfInitMethod.STATIC) {
            log.info("Publish static emvmpm qr");
            emvmpmQr = emvMpmService.findStaticMpmQrOrCreate(member.getMemberId(), merchant);
            out = MpmQrInfoResDto.staticMpmQrInfo(merchant.getMerchantName(), ZxingQrcode.base64EncodedQrImageForQrpay(emvmpmQr.getQrData()));
        } else {
            //thorw
        }
        return ResponseEntity.ok().body(out);
    }


}
