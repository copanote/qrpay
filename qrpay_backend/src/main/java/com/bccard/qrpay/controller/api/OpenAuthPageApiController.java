package com.bccard.qrpay.controller.api;

import com.bccard.qrpay.controller.api.common.QrpayApiResponse;
import com.bccard.qrpay.controller.api.dtos.*;
import com.bccard.qrpay.domain.log.QrpayLog;
import com.bccard.qrpay.domain.log.QrpayLogService;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.exception.MemberException;
import com.bccard.qrpay.exception.QrpayCustomException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.bccard.qrpay.external.bc.mci.MciService;
import com.bccard.qrpay.external.nice.NiceSmsState;
import com.bccard.qrpay.external.nice.dto.NiceSmsSessionData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/qrpay/api")
public class OpenAuthPageApiController {

    private final MemberService memberService;
    private final QrpayLogService logService;
    private final MciService mciService;
    private final ObjectMapper objectMapper;

    @RequestMapping(value = "/open/v1/member/id-check")
    @ResponseBody
    public ResponseEntity<?> idDuplicationCheck_open(@RequestBody @Validated IdDupCheckReqDto reqDto) {

        log.info("reqDto={}", reqDto);

        if (memberService.exist(reqDto.getId())) {
            throw new MemberException(QrpayErrorCode.LOGIN_ID_CONFLICT);
        }

        return ResponseEntity.ok(QrpayApiResponse.ok());
    }

    @RequestMapping(value = "open/v1/member/password-reset")
    @ResponseBody
    public ResponseEntity<?> restPassword(@RequestBody @Validated ResetPasswordReqDto reqDto)
            throws JsonProcessingException {

        String refId = reqDto.getSmsRefId();

        QrpayLog qrpayLog = logService.findById(Long.valueOf(refId));
        String sessionData = qrpayLog.getLogMessage().getBody();
        NiceSmsSessionData niceSmsSessionData = objectMapper.readValue(sessionData, NiceSmsSessionData.class);

        if (NiceSmsState.CONFIRM_COMPLETE == niceSmsSessionData.getState()) {
            throw new QrpayCustomException(QrpayErrorCode.VERIFICATION_ALREADY_CONSUMED);
        }

        Member toChangeMember = memberService.findByLoginId(niceSmsSessionData.getLoginId());

        if (!reqDto.confirmPasswordMatch()) {
            throw new MemberException(QrpayErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }
        Member m = memberService.updatePassword(toChangeMember, reqDto.getNewPassword());

        niceSmsSessionData.changeStateToConfirm();
        QrpayLog fetched =
                logService.updateLogMessageBody(qrpayLog.getId(), objectMapper.writeValueAsString(niceSmsSessionData));

        return ResponseEntity.ok(QrpayApiResponse.ok(toChangeMember, ChangePasswordResDto.of(m)));
    }

    @RequestMapping(value = "open/v1/bc/merchants")
    @ResponseBody
    public ResponseEntity<?> bcMerchants(@RequestBody @Validated BcMerchantReqDto reqDto) {

        Random random = new Random();
        List<BcMerchantInfo> result = mciService.mockBcMerchantList(reqDto.getBizNo(), reqDto.getNextKey());
        boolean hasNextRand = random.nextBoolean();
        String nextKey = "";
        if (hasNextRand) {
            BcMerchantInfo last = result.getLast();
            nextKey = last.getRegisteredAt() + "|" + last.getMerchantNo();
        }

        BcMerchantResDto resDto = BcMerchantResDto.builder()
                .hasNext(hasNextRand)
                .nextKey(nextKey)
                .size(result.size())
                .list(result)
                .build();
        return ResponseEntity.ok(QrpayApiResponse.ok(resDto));
    }
}
