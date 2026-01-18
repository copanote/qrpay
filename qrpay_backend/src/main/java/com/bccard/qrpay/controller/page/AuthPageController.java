package com.bccard.qrpay.controller.page;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.log.QrpayLog;
import com.bccard.qrpay.domain.log.QrpayLogService;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.merchant.FinancialInstitutionMerchant;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.MerchantService;
import com.bccard.qrpay.exception.QrpayCustomException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.bccard.qrpay.external.bc.mci.MciService;
import com.bccard.qrpay.external.bc.mci.dto.MCDQCOOAMO01561ResDto;
import com.bccard.qrpay.external.nice.NiceSmsRequestor;
import com.bccard.qrpay.external.nice.NiceSmsService;
import com.bccard.qrpay.external.nice.NiceSmsState;
import com.bccard.qrpay.external.nice.dto.NiceSmsSessionData;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import com.bccard.qrpay.utils.security.HashCipher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/pages")
public class AuthPageController {

    private final QrpayLogService logService;
    private final MemberService memberService;
    private final MerchantService merchantService;

    private final MciService mciService;
    private final NiceSmsService niceSmsService;
    private final ObjectMapper objectMapper;

    @GetMapping("/login")
    public String loginView(Model model) {
        return "auth/login";
    }

    @GetMapping("/auth/signup/terms")
    public String signup_terms(Model model) {
        return "auth/sign-up/signup-terms-01";
    }

    @GetMapping("/auth/signup/auth-method")
    public String signup_select_auth(Model model) throws JsonProcessingException {

        Long sequence = logService.getId();
        String sSequence = String.valueOf(sequence);

        String niceSmsEncData = niceSmsService.createNiceSmsEncData(sSequence);
        log.info("niceSmsEncData={}", niceSmsEncData);

        NiceSmsSessionData smsSessionData = NiceSmsSessionData.builder()
                .referenceId(sSequence)
                .createdAt(MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC))
                .niceSmsRequestor(NiceSmsRequestor.SIGNUP)
                .state(NiceSmsState.REQUEST_PROGRESS)
                .build();

        QrpayLog qrpayLog = QrpayLog.smsNice(sequence, objectMapper.writeValueAsString(smsSessionData));
        logService.saveLog(qrpayLog);

        model.addAttribute("niceSmsEncData", niceSmsEncData);
        model.addAttribute("niceSmsUrl", "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb");
        model.addAttribute("smsRefId", sSequence);

        return "auth/sign-up/signup-auth-method-02";
    }

    @GetMapping("/auth/signup/merchant")
    public String signup_merchant(Model model) {
        return "auth/sign-up/signup-merchant-03";
    }

    @GetMapping("/auth/signup/member")
    public String signup_member_info(Model model) {
        return "auth/sign-up/signup-member-info-04";
    }


    @GetMapping("/auth/findid/auth-method")
    public String findId_auth_method(Model model) throws JsonProcessingException {

        Long sequence = logService.getId();
        String sSequence = String.valueOf(sequence);

        String niceSmsEncData = niceSmsService.createNiceSmsEncData(sSequence);
        log.info("niceSmsEncData={}", niceSmsEncData);

        NiceSmsSessionData smsSessionData = NiceSmsSessionData.builder()
                .referenceId(sSequence)
                .createdAt(MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC))
                .niceSmsRequestor(NiceSmsRequestor.FIND_ID)
                .state(NiceSmsState.REQUEST_PROGRESS)
                .build();

        QrpayLog qrpayLog = QrpayLog.smsNice(sequence, objectMapper.writeValueAsString(smsSessionData));
        logService.saveLog(qrpayLog);

        //save
        model.addAttribute("niceSmsEncData", niceSmsEncData);
        model.addAttribute("niceSmsUrl", "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb");
        model.addAttribute("smsRefId", sSequence);

        return "auth/find-id/findid-auth-method-01";
    }

    @GetMapping("/auth/findid/merchant")
    public String findId_merchant(Model model) {
        return "auth/find-id/findid-merchant-02";
    }

    @GetMapping("/auth/findid/confirm")
    public String findId_confirm(Model model,
                                 @RequestParam String niceReferenceId,
                                 @RequestParam String bcMerchantNo) throws JsonProcessingException {

        QrpayLog qrpayLog = logService.findById(Long.valueOf(niceReferenceId));
        String sessionData = qrpayLog.getLogMessage().getBody();
        NiceSmsSessionData niceSmsSessionData = objectMapper.readValue(sessionData, NiceSmsSessionData.class);

        log.info("NiceSmsSessionData={}", niceSmsSessionData);

        if (niceSmsSessionData.getState() != NiceSmsState.CALLBACK_RECEIVED) {
            //illegal state exception
        }

        niceSmsSessionData.changeStateToConfirm();
        QrpayLog fetched = logService.updateLogMessageBody(qrpayLog.getId(), objectMapper.writeValueAsString(niceSmsSessionData));

        MCDQCOOAMO01561ResDto resDto = mciService.mockSearchBcMerchantDetails(bcMerchantNo);
        log.info("MCDQCOOAMO01561ResDto={}", resDto);

        String merchantHashedCi = HashCipher.sha256EncodedBase64(resDto.getRsvrCiV88());
        niceSmsSessionData.onUpdateMerchantProvidedHashedCi(merchantHashedCi);
        QrpayLog fetched2 = logService.updateLogMessageBody(qrpayLog.getId(), objectMapper.writeValueAsString(niceSmsSessionData));

        if (!niceSmsSessionData.getNiceProvidedHashedCi().equals(merchantHashedCi)) {
            //not matched ci;
        }

        Optional<Merchant> byBcMerchantNo = merchantService.findByBcMerchantNo(bcMerchantNo);
        if (bcMerchantNo.isEmpty()) {
            //가입하지 않음.
//            throw new IllegalStateException();
        }
        Merchant finded = byBcMerchantNo.get();
        Member finedMasterMember = memberService.findMembers(finded).stream().filter(m -> m.getRole() == MemberRole.MASTER).findFirst()
                .orElseThrow(IllegalStateException::new);

        model.addAttribute("userName", finded.getRepresentativeName());
        model.addAttribute("userId", finedMasterMember.getLoginId());

        return "auth/find-id/findid-confirm-03";
    }

    @GetMapping("/auth/findpw/check-id")
    public String findPw_auth_check_id(Model model) {
        return "auth/find-pw/findpw-checkId-01";
    }

    @GetMapping("/auth/findpw/auth-method")
    public String findPw_auth_method(Model model,
                                     @RequestParam String authId) throws JsonProcessingException {

        log.info("requestParams={}", authId);

        Member member = memberService.findByLoginId(authId);
        List<FinancialInstitutionMerchant> fiMerchants = member.getMerchant().getFiMerchants();
        FinancialInstitutionMerchant bcMerchant = fiMerchants.stream()
                .filter(f -> f.getFinancialInstitution() == FinancialInstitution.BCCARD)
                .findAny().orElseThrow(() -> new QrpayCustomException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        MCDQCOOAMO01561ResDto resDto = mciService.mockSearchBcMerchantDetails(bcMerchant.getFiMerchantNo());
        log.info("MCDQCOOAMO01561ResDto={}", resDto);


        Long sequence = logService.getId();
        String sSequence = String.valueOf(sequence);

        String niceSmsEncData = niceSmsService.createNiceSmsEncData(sSequence);
        log.info("niceSmsEncData={}", niceSmsEncData);

        NiceSmsSessionData smsSessionData = NiceSmsSessionData.builder()
                .referenceId(sSequence)
                .createdAt(MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC))
                .niceSmsRequestor(NiceSmsRequestor.PASSWORD_RESET)
                .state(NiceSmsState.REQUEST_PROGRESS)
                .loginId(authId)
                .merchantProvidedHashedCi(HashCipher.sha256EncodedBase64(resDto.getRsvrCiV88()))
                .build();

        QrpayLog qrpayLog = QrpayLog.smsNice(sequence, objectMapper.writeValueAsString(smsSessionData));
        logService.saveLog(qrpayLog);

        //save
        model.addAttribute("niceSmsEncData", niceSmsEncData);
        model.addAttribute("niceSmsUrl", "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb");
        model.addAttribute("smsRefId", sSequence);
        return "auth/find-pw/findpw-auth-method-02";
    }

    @GetMapping("/auth/findpw/change")
    public String findPw_change(Model model, @RequestParam String niceReferenceId) throws JsonProcessingException {

        QrpayLog qrpayLog = logService.findById(Long.valueOf(niceReferenceId));
        String sessionData = qrpayLog.getLogMessage().getBody();
        NiceSmsSessionData niceSmsSessionData = objectMapper.readValue(sessionData, NiceSmsSessionData.class);

        log.info("NiceSmsSessionData={}", niceSmsSessionData);

        if (niceSmsSessionData.getState() != NiceSmsState.CALLBACK_RECEIVED) {
            //illegal state exception
        }

        niceSmsSessionData.changeStateToConfirm();
        QrpayLog fetched = logService.updateLogMessageBody(qrpayLog.getId(), objectMapper.writeValueAsString(niceSmsSessionData));
        model.addAttribute("smsRefId", niceReferenceId);
        model.addAttribute("authMethod", "NICE-SMS");
        return "auth/find-pw/findpw-change-03";
    }


}
