//
//  Constant.h
//  mpm
//
//  Created by SugjinMac on 2018. 1. 31..
//  Copyright © 2018년 BCCard. All rights reserved.
//

#ifndef Constant_h
#define Constant_h

#define WEB_BASE_URL                @"/app/mpm/"
#define LOGIN_URL                   @"view/login.jsp"
#define JOIN_MEMBER_URL             @"view/merchant/memRegFrm.jsp"
#define CHANGE_PW_URL               @"view/merchant/changePassFrm.jsp"
#define MAIN_URL                    @"Main.do"
#define SUBMAIN_URL                 @"SubMain.do"
#define TRANSLIST_URL               @"TransList.do"
#define UPDATE_PAGE                 @"view/common/appDown.jsp"
#define PRIVACY_POLICY_TREATMENT    @"https://m.bccard.com/app/mobileweb/privacyProtect.do?exec=privacyPolicyTreatment"

#define IF_SERID_APP_INTRO          @"AppIntro.do"
#define IF_SERID_APP_DEVICE_UPDATE  @"AppDeviceUpdate.do"
#define IF_SERID_APP_DTCT_LOG        @"AppDtctLog.do"
#define IF_SERID_UPDATE_PUSH_TOKEN  @"AppDeviceUpdate.do"

#define TITLE_INFO          @"알림"
#define TITLE_UPDATE        @"업데이트"

#define BTN_OK              @"확인"
#define BTN_CANCEL          @"취소"
#define BTN_UPDATE          @"업데이트"
#define BTN_MOVE            @"이동"
#define BTN_SEARCH          @"조회"
#define BTN_SHARE           @"공유"
#define BTN_FINISH          @"종료"
#define BTN_CLOSE           @"닫기"
#define BTN_SETTING         @"설정"

#define MSG_UPDATE          @"업데이트 정보가 있습니다.\n최신버전으로 업데이트를 진행 하시겠습니까?"
#define MSG_UPDATE_FORCE    @"업데이트 정보가 있습니다.\n업데이트 페이지로 이동합니다."
#define MSG_FILE_MAKE_FAIL  @"이미지 파일 생성에 실패하였습니다."
#define MSG_NOT_CONNECT_NETWORK @"네트워크 연결을 확인해주세요."
#define MSG_VERSION_INFO_CHECK  @"버전정보 확인중"
#define MSG_BASE_INFO_SUCCESS   @"기본정보 수신이 완료되었습니다."
#define MSG_BASE_INFO_FAIL      @"기본정보 수신에 실패하였습니다."
#define MSG_LOGOUT              @"다시 로그인해주세요."
#define MSG_QR_SHARE_URL_FAIL   @"Qr 이미지 경로가 정상적이지 않습니다. 잠시후에 다시 시도해주세요."
#define MSG_FINISH_APP      @"앱을 종료하시겠습니까?"
#define MSG_PUSH_ALLOW      @"알림 허용이 꺼져있습니다. 단말기의 설정화면에서 'QR Pay for Shop'의 알림을 허용해주세요."

#define ALERT_TYPE_OK             1000
#define ALERT_TYPE_UPDATE         1001
#define ALERT_TYPE_FORCE_UPDATE   1002
#define ALERT_TYPE_PUSH_SETTING   1003

#define NOTIFICATION_CATEGORY @"NOTIFICATION_CATEGORY"

#define APP_ID              @"mpm"
#define DEVI_TYPE           @"I"

#endif /* Constant_h */
