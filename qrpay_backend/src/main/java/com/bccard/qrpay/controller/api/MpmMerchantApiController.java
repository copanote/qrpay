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
import com.bccard.qrpay.domain.merchant.application.port.in.ChangeMerchantNameCommand;
import com.bccard.qrpay.domain.merchant.application.port.in.ChangeMerchantNameUseCase;
import com.bccard.qrpay.domain.mpmqr.EmvMpmQrService;
import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;
import com.bccard.qrpay.domain.mpmqr.application.port.in.PublishStaticEmvMpmQrCommand;
import com.bccard.qrpay.domain.mpmqr.application.port.in.PublishStaticEmvMpmQrUseCase;
import com.bccard.qrpay.domain.mpmqr.dto.PublishBcEmvMpmQrReqDto;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.MpmQrException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.bccard.qrpay.utils.ZxingQrcode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MPM가맹점", description = "BC MPM가맹 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/qrpay/api")
public class MpmMerchantApiController {

    private final MerchantService merchantService;
    private final MemberService memberService;
    private final EmvMpmQrService emvMpmQrService;
    private final ChangeMerchantNameUseCase changeMerchantNameUseCase;
    private final PublishStaticEmvMpmQrUseCase publishStaticEmvMpmQrUseCase;

    @RequestMapping(value = "v1/member/{memberId}/employee-cancel11")
    @ResponseBody
    public ResponseEntity<?> cancel(@LoginMember Member member, @PathVariable(value = "memberId") String memberId) {
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Member byMemberId = memberService.findByMemberId(memberId);

        //        memberService.cancel();

        return ResponseEntity.ok(QrpayApiResponse.ok(member, EmployeesInfoDto.from(null)));
    }

    @RequestMapping(value = "/v1/merchant/add-employee")
    @ResponseBody
    public ResponseEntity<?> addEmployee(@LoginMember Member member, @RequestBody AddEmployeeReqDto reqDto) {
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Merchant merchant = member.getMerchant();

        log.info("reqDto={}", reqDto);

        List<Permission> permissions = reqDto.getPermissions();
        boolean permissionCancel = permissions.contains(Permission.CANCEL);

        Member newEmployee =
                memberService.addEmployee(merchant, reqDto.getLoginId(), reqDto.getPassword(), permissionCancel);

        return ResponseEntity.ok(QrpayApiResponse.ok(member, EmployeesInfoDto.from(newEmployee)));
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
        List<EmployeesInfoDto> result =
                employees.stream().map(EmployeesInfoDto::from).toList();

        return ResponseEntity.ok(QrpayApiResponse.ok(member, EmployeesInfoResDto.of(result)));
    }

    @RequestMapping(value = "/v1/merchant/info")
    @ResponseBody
    public ResponseEntity<?> merchantInfo(@LoginMember Member member) {

        Merchant merchant = member.getMerchant();

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        return ResponseEntity.ok(QrpayApiResponse.ok(member, MerchantInfoResDto.from(merchant)));
    }

    @PostMapping(value = "/v1/merchant/change-vat")
    @ResponseBody
    public ResponseEntity<?> changeVat(@LoginMember Member member, @RequestBody VatChangeReqDto reqDto) {

        Merchant merchant = member.getMerchant();

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        BigDecimal updateVat = null;
        if (reqDto.isEnableVat()) {
            updateVat = BigDecimal.valueOf(reqDto.getVatRate());
        }
        Merchant changed = merchantService.updateVat(merchant, updateVat);

        TipTaxInfoDto resDto = TipTaxInfoDto.builder()
                .tipRate(changed.getTipRate() == null ? BigDecimal.ZERO : changed.getTipRate())
                .vatRate(changed.getVatRate() == null ? BigDecimal.ZERO : changed.getVatRate())
                .build();

        return ResponseEntity.ok(QrpayApiResponse.ok(member, resDto));
    }

    @PostMapping(value = "/v1/merchant/change-tip")
    public ResponseEntity<?> changeTip(@LoginMember Member member, @RequestBody TipChangeReqDto reqDto) {

        log.info("Member={}", member.getMemberId());
        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        BigDecimal updateVat = null;
        if (reqDto.isEnableTip()) {
            updateVat = BigDecimal.valueOf(reqDto.getTipRate());
        }
        Merchant changed = merchantService.updateTip(member.getMerchant(), updateVat);

        TipTaxInfoDto resDto = TipTaxInfoDto.builder()
                .tipRate(changed.getTipRate() == null ? BigDecimal.ZERO : changed.getTipRate())
                .vatRate(changed.getVatRate() == null ? BigDecimal.ZERO : changed.getVatRate())
                .build();

        return ResponseEntity.ok(QrpayApiResponse.ok(member, resDto));
    }

    @PostMapping(value = "/v1/merchant/change-name")
    @ResponseBody
    public ResponseEntity<?> changeName(
            @LoginMember Member member, @RequestBody @Validated MerchantNameChangeReqDto req) throws Exception {

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        ChangeMerchantNameCommand command =
                new ChangeMerchantNameCommand(member.getMerchant().getMerchantId(), req.getName());
        Merchant merchant = changeMerchantNameUseCase.change(command);

        PublishStaticEmvMpmQrCommand publishCommand =
                PublishStaticEmvMpmQrCommand.staticEmvMpm(member.getMemberId(), merchant.getMerchantId(), "410");

        MpmQrPublication staticMpmQr = publishStaticEmvMpmQrUseCase.publishStatic(publishCommand);

        MpmQrInfoResDto out = MpmQrInfoResDto.staticMpmQrInfo(
                merchant.getMerchantName(), ZxingQrcode.base64EncodedQrImageForQrpay(staticMpmQr.getQrData()));

        return ResponseEntity.ok().body(QrpayApiResponse.ok(member, out));
    }

    @Operation(
            summary = "MPMQR생성",
            description = "jwt로 인증된 사용자의 가맹점의 MPMQR을 생성한다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "MPMQR생성 성공"),
                @ApiResponse(responseCode = "400", description = "Bad Request")
            })
    @PostMapping(value = "/v1/merchant/mpmqr")
    @ResponseBody
    public ResponseEntity<?> mpmqr(@LoginMember Member member, @RequestBody @Validated MpmQrInfoReqDto req)
            throws Exception {

        Merchant merchant = member.getMerchant();
        MpmQrPublication emvmpmQr;
        MpmQrInfoResDto out = null;

        log.info("MpmQrInfoReqDto={}", req.toString());
        if (req.getPim() == PointOfInitMethod.DYNAMIC) {

            if (req.getAmount() == null || req.getInstallment() == null) {
                throw new IllegalArgumentException("amount or installment should not be null");
            }

            PublishBcEmvMpmQrReqDto publishEmvMpmReqDto = PublishBcEmvMpmQrReqDto.dynamicEmvMpm(
                    member.getMemberId(), merchant, req.getAmount(), req.getInstallment(), "410");

            log.info("Publish Dynamic emvmpm qr={}", publishEmvMpmReqDto);

            emvmpmQr = emvMpmQrService.publishBcEmvMpmQr(publishEmvMpmReqDto);
            out = MpmQrInfoResDto.dynamicMpmQrInfo(
                    merchant.getMerchantName(),
                    ZxingQrcode.base64EncodedQrImageForQrpay(emvmpmQr.getQrData()),
                    emvmpmQr.getAmount(),
                    req.getInstallment());

        } else if (req.getPim() == PointOfInitMethod.STATIC) {
            log.info("Publish static emvmpm qr");
            emvmpmQr = emvMpmQrService.findStaticMpmQrOrCreate(member.getMemberId(), merchant);
            out = MpmQrInfoResDto.staticMpmQrInfo(
                    merchant.getMerchantName(), ZxingQrcode.base64EncodedQrImageForQrpay(emvmpmQr.getQrData()));
        } else {
            throw new MpmQrException(QrpayErrorCode.NOT_SUPPORT_PIM);
        }
        return ResponseEntity.ok().body(out);
    }
}
