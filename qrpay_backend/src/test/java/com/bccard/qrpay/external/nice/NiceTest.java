package com.bccard.qrpay.external.nice;

import NiceID.Check.CPClient;
import com.bccard.qrpay.external.nice.dto.NiceSmsResultDto;
import java.util.HashMap;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class NiceTest {

    private static String siteCode = "BE642";
    private static String pass = "FQ5KpmiScdli";

    @Test
    void test() {
        System.out.println("11");
        String reqNo = UUID.randomUUID().toString();
        CPClient niceCheck = new CPClient();
        //        String sRequestNumber = niceCheck.getRequestNO(reqNo);

        System.out.println(reqNo);

        String sAuthType = "";

        String popgubun = "N";
        String customize = "";

        String sGender = "";

        String sReturnUrl = "http://www.test.co.kr/checkplus_success.jsp";
        String sErrorUrl = "http://www.test.co.kr/checkplus_fail.jsp";

        String sPlainData = "7:REQ_SEQ" + reqNo.getBytes().length + ":" + reqNo + "8:SITECODE"
                + siteCode.getBytes().length + ":" + siteCode + "9:AUTH_TYPE"
                + sAuthType.getBytes().length + ":" + sAuthType + "7:RTN_URL"
                + sReturnUrl.getBytes().length + ":" + sReturnUrl + "7:ERR_URL"
                + sErrorUrl.getBytes().length + ":" + sErrorUrl + "11:POPUP_GUBUN"
                + popgubun.getBytes().length + ":" + popgubun + "9:CUSTOMIZE"
                + customize.getBytes().length + ":" + customize + "6:GENDER"
                + sGender.getBytes().length + ":" + sGender;

        int i = niceCheck.fnEncode(siteCode, pass, sPlainData);
        System.out.println(i);
        System.out.println(niceCheck.getCipherData());
        CPClient niceCheck2 = new CPClient();
        int iReturn = niceCheck2.fnDecode(siteCode, pass, niceCheck.getCipherData());
        System.out.println(iReturn);

        System.out.println(niceCheck2.getPlainData());
        System.out.println(niceCheck2.getCipherDateTime());
    }

    @Test
    void test_confirm() {
        CPClient niceCheck = new CPClient();
        //        String re =
        // "AgAFQkU2NDKSrMTziNqNeu2qg16thDAg0u7ByadrIyNsDsCc1qIHda53Q2ewpiAPjCZaNh0CimYVE/sbOrPgMJluHMQoiGhdkNiD2+AAFGOwSza0ZulhoRlsBYW76Z+PQaZVuBw/h79IR3dfjX6fDnhSdEdutX+SgCi3AUsVxrIlOYLQOEzYEQNvy7akFULWKCOb6x/qzXB3tmvkee6JfbxrBxxm2zkxz1mHLSDlmNUnQU5rzHsx7poGwnEpnRCBOccfOvKtvwDIy5MjNONQAzVyO9WaaioyuTUXecUR/HRY/Gvj/6id2u9zTErk0Z1OfXuJyVx+trOayGd02ZE2gJhSUW7ROtKNKOaXsVs92ijLjqSeRS7uzT01DeJZeJHFXmUgglfc4W9GVh7uKiOovewC7dfNqqbQ7igYJexHORiaLe0PXwROYDDmfHljM4nIdbZLxJcPvSDj6wE9qAMS2NrdcEMa823rTkN9hjjHs82vi+GIQ0yCQDocE7idcFeLMAIRdL/el45iB2oTOBmY/meJi5OHglb0+CP7LwcNNnm05xV7wpx/yQgmIoMUBAEhzHIUdna33dksIsCuAkU4kPboJpUx52tZEE/TIaBxhQJ0mjqMxon2bYyhHqvoyUGzOLFeQgHnGh/yei3iri5hND1xZKmWaVAiJanlwN49DdyDEHYhzHnry/4dvQv4zkRW71bH7cQzRfaCB6u7zCt63JwVkHGl5DHI";
        String re =
                "AgAFQkU2NDKSrMTziNqNeu2qg16thDAg0u7ByadrIyNsDsCc1qIHda53Q2ewpiAPjCZaNh0CimYVE/sbOrPgMJluHMQoiGhdkNiD2+AAFGOwSza0ZulhoRlsBYW76Z+PQaZVuBw/h79IR3dfjX6fDnhSdEdutX+SgCi3AUsVxrIlOYLQOEzYEQNvy7akFULWKCOb6x/qzXB3tmvkee6JfbxrBxxm2zkxz1mHLSDlmNUnQU5rzHsx7poGwnEpnRCBOccfOvKtvwDIy5MjNONQAzVyO9WaaioyuTUXecUR/HRY/Gvj/6id2u9zTErk0Z1OfXuJyVx+trOayGd02ZE2gJhSUW7ROtKNKOaXsVs92ijLjqSeRS7uzT01DeJZeJHFXmUgglfc4W9GVh7uKiOovewC7dfNqqbQ7igYJexHORiaLe0PXwROYDDmfHljM4nIdbZLxJcPvSDj6wE9qAMS2NrdcEMa823rTkN9hjjHs82vi+GIQ0yCQDocE7idcFeLMAIRdL/el45iB2oTOBmY/meJi5OHglb0+CP7LwcNNnm05xV7wpx/yQgmIoMUBAEhzHIUdna33dksIsCuAkU4kPboJpUx52tZEE/TIaBxhQJ0mjqMxon2bYyhHqvoyUGzOLFeQgHnGh/yei3iri5hND1xZKmWaVAiJanlwN49DdyDEHYhzHnry/4dvQv4zkRW71bH7cQzRfaCB6u7zCt63JwVkHGl5DHI";
        String rre = requestReplace(re, "encodeData");

        int iReturn = niceCheck.fnDecode(siteCode, pass, rre);
        System.out.println(iReturn);

        System.out.println(niceCheck.getPlainData());
        System.out.println(niceCheck.getCipherDateTime());

        HashMap hashMap = niceCheck.fnParse(niceCheck.getPlainData());
        System.out.println(hashMap.get("CI"));

        for (Object o : hashMap.keySet()) {
            System.out.println("key: " + o + ", value: " + hashMap.get(o));
        }

        /**
         * key: REQ_SEQ value: BE642_2025121710140967524
         * key: MOBILE_NO value: 01089733575
         * key: DI value: MC0GCCqGSIb3DQIJAyEA6miMsemXJnWaNSssL8T7D8ZYQluqIEA/8aKAq8UANDs=
         * key: MOBILE_CO value: KTF
         * key: CI value: snNuXx097WLeqIGmDH4St7hB3p9JZSaIw+s87affJeg+PqQvZ/oMELzMjX/6/RuT8uzFLvup0mSIh+lFzke7rg==
         * key: UTF8_NAME value: %EC%8B%A0%EB%8F%99%EC%9A%B1
         * key: GENDER value: 1
         * key: RES_SEQ value: MBE64220251217101532goVN
         * key: BIRTHDATE value: 19880622
         * key: NATIONALINFO value: 0
         * key: AUTH_TYPE value: M
         * key: NAME value: 신동욱
         */
        NiceSmsService niceSmsService = new NiceSmsService();

        NiceSmsResultDto niceSmsResultDto = niceSmsService.decodeNiceSmsEncData(rre);
        System.out.println(niceSmsResultDto);
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

            if (!gubun.equals("encodeData")) {
                paramValue = paramValue.replaceAll("\\+", "");
                paramValue = paramValue.replaceAll("/", "");
                paramValue = paramValue.replaceAll("=", "");
            }

            result = paramValue;
        }
        return result;
    }
}
