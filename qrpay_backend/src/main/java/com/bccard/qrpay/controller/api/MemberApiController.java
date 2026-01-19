package com.bccard.qrpay.controller.api;

import com.bccard.qrpay.config.web.argumentresolver.LoginMember;
import com.bccard.qrpay.controller.api.common.QrpayApiResponse;
import com.bccard.qrpay.controller.api.dtos.*;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.MemberException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/qrpay/api")
public class MemberApiController {

    private final MemberService memberService;

    @RequestMapping(value = "/v1/member/id-check")
    @ResponseBody
    public ResponseEntity<?> idDuplicationCheck(
            @LoginMember Member member, @RequestBody @Validated IdDupCheckReqDto reqDto) {
        log.info("Member={}", member.getMemberId());

        log.info("reqDto={}", reqDto);

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        if (memberService.exist(reqDto.getId())) {
            throw new MemberException(QrpayErrorCode.LOGIN_ID_CONFLICT);
        }

        return ResponseEntity.ok(QrpayApiResponse.ok(member));
    }

    @RequestMapping(value = "/v1/member/{memberId}/password-change")
    @ResponseBody
    public ResponseEntity<?> changePassword(
            @LoginMember Member member,
            @PathVariable("memberId") String memberId,
            @RequestBody ChangePasswordReqDto reqDto) {
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Member toChangeMember = memberService.findByMemberId(memberId);
        if (!member.getMerchant()
                .getMerchantId()
                .equals(toChangeMember.getMerchant().getMerchantId())) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        if (reqDto.passwordResue()) {
            throw new MemberException(QrpayErrorCode.DISALLOW_CURRENT_PASSWORD_REUSE);
        }

        if (!reqDto.confirmPasswordMatch()) {
            throw new MemberException(QrpayErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }
        Member m = memberService.updatePassword(toChangeMember, reqDto.getNewPassword());
        return ResponseEntity.ok(QrpayApiResponse.ok(member, ChangePasswordResDto.of(m)));
    }

    @RequestMapping(value = "/v1/member/{memberId}/employee-password-change")
    @ResponseBody
    public ResponseEntity<?> changePasswordEmployee(
            @LoginMember Member member,
            @PathVariable("memberId") String memberId,
            @RequestBody ChangePasswordReqDto reqDto) {
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Member toChangeMember = memberService.findByMemberId(memberId);
        if (!member.getMerchant()
                .getMerchantId()
                .equals(toChangeMember.getMerchant().getMerchantId())) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        if (!reqDto.confirmPasswordMatch()) {
            throw new MemberException(QrpayErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        Member m = memberService.updatePassword(toChangeMember, reqDto.getNewPassword());

        return ResponseEntity.ok(QrpayApiResponse.ok(member, ChangePasswordResDto.of(m)));
    }

    @RequestMapping(value = "/v1/member/{memberId}/employee-cancel-permission-change")
    @ResponseBody
    public ResponseEntity<?> changePermissionToCancel(
            @LoginMember Member member,
            @PathVariable("memberId") String memberId,
            @RequestBody CancelPermissionUpdateReqResDto reqDto) {
        log.info("Member={}", member.getMemberId());
        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Member toChangeMember = memberService.findByMemberId(memberId);
        if (!member.getMerchant()
                .getMerchantId()
                .equals(toChangeMember.getMerchant().getMerchantId())) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        toChangeMember = memberService.updatePermissionToCancel(toChangeMember, reqDto.isPermissionToCancel());
        return ResponseEntity.ok(QrpayApiResponse.ok(member, EmployeesInfoDto.from(toChangeMember)));
    }

    @RequestMapping(value = "/v1/member/{memberId}/employee-status-change")
    @ResponseBody
    public ResponseEntity<?> changeMemberStatus(
            @LoginMember Member member,
            @PathVariable("memberId") String memberId,
            @RequestBody ChangeMemberStatusReqDto reqDto) {
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Member toChangeMember = memberService.findByMemberId(memberId);
        if (!member.getMerchant()
                .getMerchantId()
                .equals(toChangeMember.getMerchant().getMerchantId())) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        toChangeMember = memberService.updateStatus(toChangeMember, reqDto.getRequestStatus());

        return ResponseEntity.ok(QrpayApiResponse.ok(member, EmployeesInfoDto.from(toChangeMember)));
    }

    @RequestMapping(value = "/v1/member/{memberId}/employee-cancel")
    @ResponseBody
    public ResponseEntity<?> cancelEmployee(@LoginMember Member member, @PathVariable("memberId") String memberId) {
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Member canceled = memberService.cancelEmployee(member, memberId);

        return ResponseEntity.ok(QrpayApiResponse.ok(
                member, ChangeMemberStatusResDto.of(canceled.getStatus(), canceled.getWithdrawalAt())));
    }
}
