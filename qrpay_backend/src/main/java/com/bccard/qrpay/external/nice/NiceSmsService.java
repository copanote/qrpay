package com.bccard.qrpay.external.nice;

import NiceID.Check.CPClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NiceSmsService {

    public static final String SITE_CODE = "BE642";      // NICE로부터 부여받은 사이트 코드
    public static final String SITE_PW = "FQ5KpmiScdli"; // NICE로부터 부여받은 사이트 패스워드

    public void initNiceSms(String requestNo) {
        CPClient niceCheck = new CPClient();

        // 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로
        // 업체에서 적절하게 변경하여 쓰거나, 아래와 같이 생성한다.
        String sRequestNumber = niceCheck.getRequestNO(requestNo);

        String sAuthType = ""; // 없으면 기본 선택화면, M: 핸드폰, C: 신용카드, X: 공인인증서
        String popgubun = "N"; //Y : 취소버튼 있음 / N : 취소버튼 없음
        String customize = ""; //없으면 기본 웹페이지 / Mobile : 모바일페이지
        String sGender = "";   //없으면 기본 선택 값, 0 : 여자, 1 : 남자

        String sReturnUrl = "http://www.test.co.kr/checkplus_success.jsp";
        String sErrorUrl = "http://www.test.co.kr/checkplus_fail.jsp";

        String sPlainData =
                "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
                        "8:SITECODE" + SITE_CODE.getBytes().length + ":" + SITE_CODE +
                        "9:AUTH_TYPE" + sAuthType.getBytes().length + ":" + sAuthType +
                        "7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl +
                        "7:ERR_URL" + sErrorUrl.getBytes().length + ":" + sErrorUrl +
                        "11:POPUP_GUBUN" + popgubun.getBytes().length + ":" + popgubun +
                        "9:CUSTOMIZE" + customize.getBytes().length + ":" + customize +
                        "6:GENDER" + sGender.getBytes().length + ":" + sGender;
        int result = niceCheck.fnEncode(SITE_CODE, SITE_PW, sPlainData);
        switch (result) {
            case 0:
                break;
            case -1:


        }

    }


}
