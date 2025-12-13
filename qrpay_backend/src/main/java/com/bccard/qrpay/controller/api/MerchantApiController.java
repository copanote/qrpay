package com.bccard.qrpay.controller.api;


import com.bccard.qrpay.auth.domain.CustomUserDetails;
import com.bccard.qrpay.auth.service.CustomPasswordEncoder;
import com.bccard.qrpay.controller.api.common.QrpayApiResponse;
import com.bccard.qrpay.controller.api.dtos.*;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.member.Permission;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.MerchantService;
import com.bccard.qrpay.domain.mpmqr.EmvMpmService;
import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;
import com.bccard.qrpay.domain.mpmqr.dto.PublishBcEmvMpmQrReqDto;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.MemberException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.bccard.qrpay.utils.ZxingQrcode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final EmvMpmService emvMpmService;
    private final CustomPasswordEncoder customPasswordEncoder;

    @RequestMapping(value = "/v1/merchant/{merchantId}/add-employee")
    @ResponseBody
    public ResponseEntity<?> addEmployee(
            @PathVariable("merchantId") String merchantId,
            @RequestBody AddEmployeeReqDto reqDto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Merchant merchant = member.getMerchant();
        if (!merchant.getMerchantId().equals(merchantId)) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        if (memberService.exist(reqDto.getLoginId())) {
            //exception
        }

        //id규칙 검증

        if (!reqDto.getPassword().equals(reqDto.getConfirmPassword())) {
            //exception
        }
        //Password 규칙검증

        List<Permission> permissions = reqDto.getPermissions();
        boolean permissionCancel = permissions.contains(Permission.CANCEL);

        Member newMem = Member.createEmployee()
                .merchant(merchant)
                .memberId(memberService.createNewMemberId())
                .loginId(reqDto.getLoginId())
                .hashedPassword(customPasswordEncoder.encode(reqDto.getPassword()))
                .permissionToCancel(permissionCancel)
                .build();

        Member newOne = memberService.save(newMem);

        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "/v1/merchant/{merchantId}/employees")
    @ResponseBody
    public ResponseEntity<?> employees(@PathVariable("merchantId") String merchantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        Merchant merchant = member.getMerchant();
        if (!merchant.getMerchantId().equals(merchantId)) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        List<Member> employees = memberService.findMemberByRole(merchant, MemberRole.EMPLOYEE);

        if (employees.isEmpty()) {
            throw new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND);
        }

        List<EmployeesInfoDto> result = employees.stream().map(EmployeesInfoDto::from).toList();

        return ResponseEntity.ok(QrpayApiResponse.ok(result));
    }


    @RequestMapping(value = "/v1/merchant/{merchantId}/info")
    @ResponseBody
    public ResponseEntity<?> merchantInfo(@PathVariable("merchantId") String merchantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        Merchant merchant = member.getMerchant();

        if (!merchant.getMerchantId().equals(merchantId)) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        return ResponseEntity.ok(MerchantInfoResDto.from(merchant));
    }


    @PostMapping(value = "/v1/merchant/{merchantId}/change-vat")
    @ResponseBody
    public ResponseEntity<?> changeVat(
            @PathVariable("merchantId") String merchantId,
            @RequestBody VatChangeReqDto reqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Authenticated Member={}", member.getMemberId());

        Merchant merchant = member.getMerchant();

        if (!merchant.getMerchantId().equals(merchantId)) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

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

    @PostMapping(value = "/v1/merchant/{merchantId}/change-tip")
    public ResponseEntity<?> changeTip(
            @PathVariable("merchantId") String merchantId,
            @RequestBody TipChangeReqDto reqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
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


    @PostMapping(value = "/v1/merchant/{merchantId}/change-name")
    @ResponseBody
    public ResponseEntity<?> changeName(
            @PathVariable("merchantId") String merchantId,
            @RequestBody @Validated MerchantNameChangeReqDto req) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
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

        MpmQrPublication staticMpmQr = emvMpmService.publishBcEmvMpmQr(reqEmvMpm);
        MpmQrInfoResDto out = MpmQrInfoResDto.staticMpmQrInfo(merchant.getMerchantName(), ZxingQrcode.base64EncodedQrImageForQrpay(staticMpmQr.getQrData()));

        return ResponseEntity.ok().body(out);
    }

    @PostMapping(value = "/v1/merchant/{merchantId}/mpmqr")
    @ResponseBody
    public ResponseEntity<?> mpmqr(
            @PathVariable("merchantId") String merchantId,
            @RequestBody @Validated MpmQrInfoReqDto req) throws Exception {

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
