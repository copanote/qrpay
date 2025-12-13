package com.bccard.qrpay.controller.api;


import com.bccard.qrpay.auth.domain.CustomUserDetails;
import com.bccard.qrpay.auth.service.CustomPasswordEncoder;
import com.bccard.qrpay.controller.api.common.QrpayApiResponse;
import com.bccard.qrpay.controller.api.dtos.CancelPermissionUpdateReqResDto;
import com.bccard.qrpay.controller.api.dtos.ChangeMemberStatusReqDto;
import com.bccard.qrpay.controller.api.dtos.ChangeMemberStatusResDto;
import com.bccard.qrpay.controller.api.dtos.ChangePasswordReqDto;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/qrpay/api")
public class MemberApiController {

    private final MemberService memberService;
    private final CustomPasswordEncoder customPasswordEncoder;


    @RequestMapping(value = "/v1/member/{memberId}/employee-password-change")
    @ResponseBody
    public ResponseEntity<?> changePassword(
            @PathVariable("memberId") String memberId,
            @RequestBody ChangePasswordReqDto reqDto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Member toChangeMember = memberService.findByMemberId(memberId);
        if (!member.getMerchant().getMerchantId().equals(toChangeMember.getMerchant().getMerchantId())) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }


        String encodedCurrentPassword = customPasswordEncoder.encode(reqDto.getCurrentPassword());
        if (!toChangeMember.getHashedPassword().equals(encodedCurrentPassword)) {

        }

        if (!reqDto.getNewPassword().equals(reqDto.getConfirmPassword())) {
            //throw
        }


        Member m = memberService.updatePassword(toChangeMember, reqDto.getNewPassword());

        return ResponseEntity.ok(QrpayApiResponse.ok(CancelPermissionUpdateReqResDto.of(toChangeMember.getPermissionToCancel())));
    }


    @RequestMapping(value = "/v1/member/{memberId}/employee-password-change")
    @ResponseBody
    public ResponseEntity<?> changePasswordEmployee(
            @PathVariable("memberId") String memberId,
            @RequestBody ChangePasswordReqDto reqDto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Member toChangeMember = memberService.findByMemberId(memberId);
        if (!member.getMerchant().getMerchantId().equals(toChangeMember.getMerchant().getMerchantId())) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        if (!reqDto.getNewPassword().equals(reqDto.getConfirmPassword())) {
            //throw
        }

        Member m = memberService.updatePassword(toChangeMember, customPasswordEncoder.encode(reqDto.getNewPassword()));

        return ResponseEntity.ok(QrpayApiResponse.ok(CancelPermissionUpdateReqResDto.of(toChangeMember.getPermissionToCancel())));
    }

    @RequestMapping(value = "/v1/member/{memberId}/cancel-permission-change")
    @ResponseBody
    public ResponseEntity<?> changePermissionToCancel(
            @PathVariable("memberId") String memberId,
            @RequestBody CancelPermissionUpdateReqResDto reqDto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Member toChangeMember = memberService.findByMemberId(memberId);
        if (!member.getMerchant().getMerchantId().equals(toChangeMember.getMerchant().getMerchantId())) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        toChangeMember = memberService.updatePermissionToCancel(toChangeMember, reqDto.isPermissionToCancel());

        return ResponseEntity.ok(QrpayApiResponse.ok(CancelPermissionUpdateReqResDto.of(toChangeMember.getPermissionToCancel())));
    }

    @RequestMapping(value = "/v1/member/{memberId}/status-change")
    @ResponseBody
    public ResponseEntity<?> changeMemberStatus(
            @PathVariable("memberId") String memberId,
            @RequestBody ChangeMemberStatusReqDto reqDto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.qrpayMember();
        log.info("Member={}", member.getMemberId());

        if (member.getRole() != MemberRole.MASTER) {
            throw new AuthException(QrpayErrorCode.INVALID_AUTHORIZATION);
        }

        Member toChangeMember = memberService.findByMemberId(memberId);
        if (!member.getMerchant().getMerchantId().equals(toChangeMember.getMerchant().getMerchantId())) {
            throw new AuthException(QrpayErrorCode.UNMATCHED_AUTHENTICATE);
        }

        toChangeMember = memberService.updateStatus(toChangeMember, reqDto.getRequestStatus());

        return ResponseEntity.ok(
                QrpayApiResponse.ok(
                        ChangeMemberStatusResDto.of(toChangeMember.getStatus(), toChangeMember.getLastModifiedAt())
                )
        );
    }


}
