/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : Constant
 * 작성자명   : 20170448
 * 상세설명   : Constant 변수 정의 클래스
 * 적용범위   : mpm
 * 작성일자   : 2017.12.13
 * @요청자 :
 * @결재자 :
 * @개발자 :
 ************************** 수정이력 ********************************************************
 * 수정일자   :
 * 수정내용   :
 * @요청자 :
 * @결재자 :
 * @개발자 :
 ****************************************************************************************/

package com.bccard.mpm.common;

import com.bccard.mpm.BuildConfig;
import com.bccard.mpm.network.ServerInfo;

public class Constant {
	public static boolean FINISH_APP = false;		// App 종료

//	public final static String APP_PACKAGENAME = "com.bccard.mpm";
	public final static boolean DROID_X_RUN = true;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///

	public final static String UASTRING = "/Qrpay_Android/";

	public final static String DOMAIN = BuildConfig.SERVER_DEV ? "isrnd3.bccard.com" : "qr.bcqrcpay.com" ;
	public final static String WEB_VIEW_BASE_URL = ServerInfo.SERVER_URL + ServerInfo.SERVER_PORT + ServerInfo.SERVER_CONTEXT;
//	public final static String MAIN_URL = ServerInfo.SERVER_URL + ServerInfo.SERVER_PORT + "/app/mpm/Main.do";		//파일럿버전

	public final static String MAIN_URL = ServerInfo.SERVER_URL + ServerInfo.SERVER_PORT + ServerInfo.SERVER_CONTEXT + "pages/home/mpmqr";
	public final static String LOGIN_URL = ServerInfo.SERVER_URL + ServerInfo.SERVER_PORT + ServerInfo.SERVER_CONTEXT + "pages/login";

	public final static String OLD_MAIN_URL = ServerInfo.SERVER_URL + ServerInfo.SERVER_PORT + ServerInfo.SERVER_CONTEXT + "Main.do";			//메인
	public final static String SUB_MAIN_URL = ServerInfo.SERVER_URL + ServerInfo.SERVER_PORT + ServerInfo.SERVER_CONTEXT + "SubMain.do";	//서브메인
	public final static String API_URL = "/OauthLogin.api";

	public final static String PRIVACY_POLICY_TREATMENT_URL = "https://m.bccard.com/app/mobileweb/privacyProtect.do?exec=privacyPolicyTreatment";
	public final static String SMS_CERT_DOMAIN = "nice.checkplus.co.kr";
//	public final static String FILE_PATH = "/mpm/";

//	public final static int SHOW_PROGRESS = 1;
//	public final static int HIDE_PROGRESS = 2;

	public final static String NFILTER_MODE_CHAR = "eng";		// 영문 키패드
	public final static String NFILTER_MODE_NUM = "number";		// 숫자 키패드

	public final static int SYSTEM_KEYBOARD_DIFF = 150;		// 키패드 상태확인을 위한 사이즈 버퍼

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 탐지로그코드
	public static boolean DETECT_FINISH_APP = false;

	public final static String DETECT_CODE_FAKE = "104";		// 위변조 감지

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Validation
	public final static int GET_DEVICE_UPDATE_HANDLE_DELAY_TIME = 1000*30;

	public final static String UPDATE_DEVICE_AFFI_CO_ID = "BCQRCPAY";		// 제휴사 ID

	public final static String WV_HEADER_GUBUN_KEY = "app_n";
	public final static String WV_HEADER_GUBUN_VALUE = "MPM";

	public final static int WM_WV_ERROR = 0x005;						// WebView Callback Key
	public final static int WM_WV_ERROR_HOST_LOOKUP = 0x006;
	public final static int WM_WV_ONLOAD = 0x010;
	public final static int WM_WV_LOGOUT = 0x030;
	public final static int WM_WV_QR_SHARE = 0x040;
	public final static int WM_WV_QR_LOAD = 0x050;
	public final static int WM_WV_SHOW_NFILTER_KEYBOARD = 0x060;
	public final static int WM_WV_HIDE_NFILTER_KEYBOARD = 0x061;
	public final static int WM_WV_SET_NFILTER_PUBLIC_KEY = 0x070;
	public final static int WM_WV_GET_DEVICE = 0x080;
	public final static int WM_WV_GET_DECAL_CODE = 0x090;
    public final static int WM_WV_GET_TRNS_DATA = 0x0A0;
	public final static int WM_WV_SHOW_LOADING = 0x0B0;
	public final static int WM_WV_DISMISS_LOADING = 0x0C0;


//	public final static int CONNECT_LTE = 0x001;
//	public final static int CONNECT_WIFI = 0x002;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Extra

	public final static String EXTRA_QR_SHARE_URL = "QR_SHARE_URL";
	public final static String EXTRA_QR_LOGOUT = "QR_LOGOUT";
	public final static String EXTRA_QR_ERROR = "QR_ERROR";

	public final static String EXTRA_PUSH_LIST_KEY = "PUSH_LIST_KEY";
	public final static String EXTRA_PUSH_NOTI_CLICK_VALUE = "PUSH_NOTI_CLICK_VALUE";

	public final static String EXTRA_DECAL_CODE = "DECAL_CODE";

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Activity Result Code	

	public final static int REQAC_PERMISSION_INTRO = 0x0001;
	public final static int REQAC_PERMISSION_PERMISSION = 0x0003;
	public final static int REQAC_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x0005;
	public final static int REQAC_QR_SHARE = 0x0011;
	public final static int REQAC_QR_READER = 0x0013;
	public final static int REQAC_QR_SCAN = 0x0014;

    public final static int REQAC_RECIPT = 0x0015;
	public final static int REQAC_QR_SCAN_PERMISSION = 0x0016;

	public final static int REQAC_PERMISSION_APP_SETTING = 0x0017;

//	public final static int REQAC_PERMISSION_MANAGE_STORAGE = 0x0018;

//	public final static int REQAC_PUSH_AGREEMENT_INTRO = 0x0012;

	public final static String ACTION_PUSH_INTENT_SERVICE_RESPONSE = "com.bccard.mpm.push.intentservice.RESPONSE";
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Fragment State
//	public final static int STATE_FRAGMENT_PAGE_1 = 0x000;
//	public final static int STATE_FRAGMENT_PAGE_2 = 0x001;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Preference

	public final static String PREF_MPM_INFO = "MPM_INFO";

	public final static String PREF_MPM_KEY_INSTALL_FLAG = "MPM_KEY_INSTALL_FLAG";
	public final static String PREF_MPM_KEY_UUID = "MPM_KEY_UUID";
	public final static String PREF_MPM_KEY_QR = "MPM_KEY_QR";
	public final static String PREF_MPM_KEY_MERNM = "MPM_KEY_MERNM";
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Droid-X Notification
	public final static String DROIDX_CHANNEL_ID = "DROID_X";
	public final static int DROIDX_NOTI_ID = 199;


//Loading Type
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public final static int LOADING_TYPE_GENERAL = 0;
	public final static int LOADING_TYPE_SECRET = 1;


	public final static String LOCAL_EVENT_DROIDX_DETECTED_REMOTE_APP = "net.nshc.android.droidx_addon_library.DETECT_REMOTE_APP";
	public final static String LOCAL_EVENT_DROIDX_FINISH_APP = "net.nshc.android.droidx.FINISH_APP";

}
