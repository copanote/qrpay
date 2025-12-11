package com.bccard.qrpay.controller.api;


import com.bccard.qrpay.auth.domain.CustomUserDetails;
import com.bccard.qrpay.controller.api.common.QrpayApiResponse;
import com.bccard.qrpay.controller.api.dtos.CancelPermissionUpdateReqResDto;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.code.AuthErrorCode;
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

    @RequestMapping(value = "/v1/member/{memberId}/change-cancel-permission")
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
            throw new AuthException(AuthErrorCode.INVALID_AUTHORIZATION);
        }

        Member toChangeMember = memberService.findByMemberId(memberId);
        if (!member.getMerchant().getMerchantId().equals(toChangeMember.getMerchant().getMerchantId())) {
            throw new AuthException(AuthErrorCode.UNMATCHED_AUTHENTICATE);
        }

        boolean nowPermission = toChangeMember.getPermissionToCancel() != null && toChangeMember.getPermissionToCancel();

        if (nowPermission != reqDto.isPermissionToCancel()) {
            toChangeMember = memberService.updatePermissionToCancel(toChangeMember, reqDto.isPermissionToCancel());
        }

        return ResponseEntity.ok(QrpayApiResponse.ok(CancelPermissionUpdateReqResDto.of(toChangeMember.getPermissionToCancel())));
    }

}
