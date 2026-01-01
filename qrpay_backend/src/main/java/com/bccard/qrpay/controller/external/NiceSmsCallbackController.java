package com.bccard.qrpay.controller.external;


import com.bccard.qrpay.domain.log.QrpayLog;
import com.bccard.qrpay.domain.log.QrpayLogService;
import com.bccard.qrpay.external.nice.NiceSmsRequestor;
import com.bccard.qrpay.external.nice.NiceSmsService;
import com.bccard.qrpay.external.nice.dto.NiceSmsResultDto;
import com.bccard.qrpay.external.nice.dto.NiceSmsSessionData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/external")
public class NiceSmsCallbackController {

    private final NiceSmsService niceSmsService;
    private final QrpayLogService logService;
    private final ObjectMapper objectMapper;

    @GetMapping("/nice/sms/result")
    public String resultCallback(Model model,
                                 @RequestParam("EncodeData") String encodeData,
                                 @RequestParam("smsRefId") String refId) throws JsonProcessingException {

        String niceEncData = requestReplace(encodeData, "encodeData");
        NiceSmsResultDto niceSmsResultDto = niceSmsService.decodeNiceSmsEncData(niceEncData);
        String referenceId = niceSmsResultDto.getReferenceId();
        referenceId = refId; //TEMP

        QrpayLog qrpayLog = logService.findById(Long.valueOf(referenceId));
        String sessionData = qrpayLog.getLogMessage().getBody();
        NiceSmsSessionData niceSmsSessionData = objectMapper.readValue(sessionData, NiceSmsSessionData.class);

        log.info("NiceSmsSessionData={}", niceSmsResultDto);

        niceSmsSessionData.changeStateToCallback(niceSmsResultDto.getCi());

        QrpayLog fetched = logService.updateLogMessageBody(qrpayLog.getId(), objectMapper.writeValueAsString(niceSmsSessionData));
        NiceSmsRequestor niceSmsRequestor = niceSmsSessionData.getNiceSmsRequestor();
        String next = niceSmsRequestor.getNextView();

        if (NiceSmsRequestor.PASSWORD_RESET == niceSmsRequestor) {
            String merCi = niceSmsSessionData.getMerchantProvidedHashedCi();
            String niceCi = niceSmsSessionData.getNiceProvidedHashedCi();
            if (!merCi.equals(niceCi)) {
                next = niceSmsRequestor.getPrevView();
                model.addAttribute("errMessage", "불일치 ");

            }
        }


        model.addAttribute("smsRefId", fetched.getId());
        return next;
    }

    private String requestReplace(String paramValue, String gubun) {
        String result = "";

        if (paramValue != null) {

            paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

            paramValue = paramValue.replaceAll("\\*", "");
            paramValue = paramValue.replaceAll("\\?", "");
            paramValue = paramValue.replaceAll("\\[", "");
            paramValue = paramValue.replaceAll("\\{", "");
            paramValue = paramValue.replaceAll("\\(", "");
            paramValue = paramValue.replaceAll("\\)", "");
            paramValue = paramValue.replaceAll("\\^", "");
            paramValue = paramValue.replaceAll("\\$", "");
            paramValue = paramValue.replaceAll("'", "");
            paramValue = paramValue.replaceAll("@", "");
            paramValue = paramValue.replaceAll("%", "");
            paramValue = paramValue.replaceAll(";", "");
            paramValue = paramValue.replaceAll(":", "");
            paramValue = paramValue.replaceAll("-", "");
            paramValue = paramValue.replaceAll("#", "");
            paramValue = paramValue.replaceAll("--", "");
            paramValue = paramValue.replaceAll("-", "");
            paramValue = paramValue.replaceAll(",", "");

            if (gubun != "encodeData") {
                paramValue = paramValue.replaceAll("\\+", "");
                paramValue = paramValue.replaceAll("/", "");
                paramValue = paramValue.replaceAll("=", "");
            }

            result = paramValue;

        }
        return result;
    }
}
