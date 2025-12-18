package com.bccard.qrpay.controller.api;


import com.bccard.qrpay.config.web.argumentresolver.LoginMember;
import com.bccard.qrpay.controller.api.common.QrpayApiResponse;
import com.bccard.qrpay.controller.api.dtos.*;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.member.Permission;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.MerchantService;
import com.bccard.qrpay.domain.mpmqr.EmvMpmQrService;
import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;
import com.bccard.qrpay.domain.mpmqr.dto.PublishBcEmvMpmQrReqDto;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.bccard.qrpay.utils.ZxingQrcode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/qrpay/api")
public class MerchantApiController {

    private final MerchantService merchantService;
    private final MemberService memberService;
    private final EmvMpmQrService emvMpmQrService;

    @RequestMapping(value = "/v1/merchant/add-employee")
    @ResponseBody
    public ResponseEntity<?> addEmployee(
            @LoginMember Member member,
            @RequestBody AddEmployeeReqDto reqDto
    ) {
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Merchant merchant = member.getMerchant();

        List<Permission> permissions = reqDto.getPermissions();
        boolean permissionCancel = permissions.contains(Permission.CANCEL);

        Member newEmployee = memberService.addEmployee(merchant, reqDto.getLoginId(), reqDto.getPassword(), permissionCancel);

        return ResponseEntity.ok(QrpayApiResponse.ok(EmployeesInfoDto.from(newEmployee)));
    }


    @RequestMapping(value = "/v1/merchant/employees")
    @ResponseBody
    public ResponseEntity<?> employees(@LoginMember Member member) {
        log.info("Member={}", member.getMemberId());

        Merchant merchant = member.getMerchant();

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        List<Member> employees = memberService.findMemberByRole(merchant, MemberRole.EMPLOYEE);
        List<EmployeesInfoDto> result = employees.stream().map(EmployeesInfoDto::from).toList();

        return ResponseEntity.ok(QrpayApiResponse.ok(EmployeesInfoResDto.of(result)));
    }


    @RequestMapping(value = "/v1/merchant/info")
    @ResponseBody
    public ResponseEntity<?> merchantInfo(@LoginMember Member member) {

        Merchant merchant = member.getMerchant();

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        return ResponseEntity.ok(QrpayApiResponse.ok(MerchantInfoResDto.from(merchant)));
    }


    @PostMapping(value = "/v1/merchant/change-vat")
    @ResponseBody
    public ResponseEntity<?> changeVat(
            @LoginMember Member member,
            @RequestBody VatChangeReqDto reqDto) {

        Merchant merchant = member.getMerchant();

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        BigDecimal updateVat = null;
        if (reqDto.isEnableVat()) {
            updateVat = BigDecimal.valueOf(reqDto.getVatRate());
        }
        Merchant changed = merchantService.updateVat(merchant, updateVat);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/v1/merchant/change-tip")
    public ResponseEntity<?> changeTip(
            @LoginMember Member member,
            @RequestBody TipChangeReqDto reqDto) {

        log.info("Member={}", member.getMemberId());
        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
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
    public ResponseEntity<?> changeName(
            @LoginMember Member member,
            @RequestBody @Validated MerchantNameChangeReqDto req) throws Exception {

        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Merchant merchant = merchantService.updateMerchantName(member.getMerchant(), req.getName());
        PublishBcEmvMpmQrReqDto reqEmvMpm = PublishBcEmvMpmQrReqDto.staticEmvMpm(
                member.getMemberId(),
                merchant,
                "410"
        );

        MpmQrPublication staticMpmQr = emvMpmQrService.publishBcEmvMpmQr(reqEmvMpm);
        MpmQrInfoResDto out = MpmQrInfoResDto.staticMpmQrInfo(merchant.getMerchantName(), ZxingQrcode.base64EncodedQrImageForQrpay(staticMpmQr.getQrData()));

        return ResponseEntity.ok().body(out);
    }

    @PostMapping(value = "/v1/merchant/mpmqr")
    @ResponseBody
    public ResponseEntity<?> mpmqr(
            @LoginMember Member member,
            @RequestBody @Validated MpmQrInfoReqDto req) throws Exception {

        Merchant merchant = member.getMerchant();
        MpmQrPublication emvmpmQr;
        MpmQrInfoResDto out = null;

        log.info("MpmQrInfoReqDto={}", req.toString());
        if (req.getPim() == PointOfInitMethod.DYNAMIC) {

            if (req.getAmount() == null || req.getInstallment() == null) {
                throw new IllegalArgumentException("amount or installment should not be null");
            }

            PublishBcEmvMpmQrReqDto publishEmvMpmReqDto = PublishBcEmvMpmQrReqDto.dynamicEmvMpm(
                    member.getMemberId(),
                    merchant,
                    req.getAmount(),
                    req.getInstallment(),
                    "410"
            );

            log.info("Publish Dynamic emvmpm qr={}", publishEmvMpmReqDto);

            emvmpmQr = emvMpmQrService.publishBcEmvMpmQr(publishEmvMpmReqDto);
            out = MpmQrInfoResDto.dynamicMpmQrInfo(
                    merchant.getMerchantName(),
                    ZxingQrcode.base64EncodedQrImageForQrpay(emvmpmQr.getQrData()),
                    emvmpmQr.getAmount(),
                    req.getInstallment()
            );

        } else if (req.getPim() == PointOfInitMethod.STATIC) {
            log.info("Publish static emvmpm qr");
            emvmpmQr = emvMpmQrService.findStaticMpmQrOrCreate(member.getMemberId(), merchant);
            out = MpmQrInfoResDto.staticMpmQrInfo(merchant.getMerchantName(), ZxingQrcode.base64EncodedQrImageForQrpay(emvmpmQr.getQrData()));
        } else {
            //thorw
        }
        return ResponseEntity.ok().body(out);
    }

}
