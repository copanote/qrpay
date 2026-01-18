package com.bccard.qrpay.external.nice.dto;

import com.bccard.qrpay.external.nice.NiceSmsRequestor;
import com.bccard.qrpay.external.nice.NiceSmsState;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import com.bccard.qrpay.utils.security.HashCipher;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NiceSmsSessionData {
    private String referenceId;
    private NiceSmsState state;
    private String createdAt;
    private NiceSmsRequestor niceSmsRequestor;
    private String loginId;
    private String merchantProvidedHashedCi;
    private String niceProvidedHashedCi;
    private String callbackAt;
    private String confirmAt;

    public void changeStateToCallback(String ci) {
        this.state = NiceSmsState.CALLBACK_RECEIVED;
        this.niceProvidedHashedCi = HashCipher.sha256EncodedBase64(ci);
        this.callbackAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC);
    }

    public void changeStateToConfirm() {
        this.state = NiceSmsState.CONFIRM_COMPLETE;
        this.confirmAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC);
    }

    public void onUpdateMerchantProvidedHashedCi(String merchantProvidedHashedCi) {
        this.merchantProvidedHashedCi = merchantProvidedHashedCi;
    }
}
