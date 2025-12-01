package com.bccard.qrpay.utils;

import java.util.Currency;
import org.apache.commons.lang3.StringUtils;

public class CurrencyUtil {

    /**
     * Currency의 numeric code로 객체 생성
     *
     * @param numericCode
     * @return
     */
    public static Currency fromNumericCode(String numericCode) {
        if (StringUtils.isEmpty(numericCode)) {
            return null;
        }

        return Currency.getAvailableCurrencies().stream()
                .filter(c -> c.getNumericCode() == Integer.parseInt(numericCode))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Not Found Currency"));
    }

    public static String toNumericCode(Currency data) {
        if (data == null) {
            return null;
        }

        return StringUtils.leftPad(String.valueOf(data.getNumericCode()), 3, "0");
    }

    /**
     * 주어진 ISO 3166 국가 코드에 해당하는 Currency 객체를 반환
     *
     * @param countryCode 두 자리의 ISO 3166 국가 코드 (예: "TH", "MY")
     * @return 해당 국가의 Currency 객체
     */
    public static Currency fromCountryCode(String countryCode) {
        //        // 국가 코드로 Locale 객체를 생성
        //        Locale locale = CountryCodeUtil.alpha2ToLocale(countryCode);
        //        if (locale == null) {
        //            return null;
        //        }

        // 생성된 Locale을 기반으로 Currency 인스턴스를 반환
        return Currency.getInstance("");
    }
}
