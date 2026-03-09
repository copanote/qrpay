/****************************************************************************************
 * This application is Copyright (C) 2015, Undine Soft. All Right Reserved.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : ServerInfo
 * 작성자명   : 20170448
 * 상세설명   : Server 관련 기본 정보를 가지는 클래스
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

package com.bccard.mpm.network;

import com.bccard.mpm.BuildConfig;

public class ServerInfo {

/*************************************************************
** SERVER INFO
*************************************************************/

/////////////////////////////////////////////////////////////////
// 공통 코드

	public final static String APP_ID = "mpm";		// 앱 아이디 (mpm)
	public final static String DEVI_TYPE = "A";		// 앱 타입(A:안드로이드, I:IOS)

	public final static String RES_ERROR_CODE = "AND_ERROR";

/////////////////////////////////////////////////////////////////
// Server Info
	// Qr Dev/Real Server Info	//http : 35080, https : 35443
//	public final static String SERVER_URL= BuildConfig.SERVER_DEV?"https://qrdev.bcqrcpay.com":"https://qr.bcqrcpay.com";
//	public final static String SERVER_PORT = BuildConfig.SERVER_DEV?":35443":"";

	public final static String SERVER_URL= BuildConfig.SERVER_DEV ? "https://isrnd3.bccard.com" : "https://qr.bcqrcpay.com";
	public final static String SERVER_PORT = BuildConfig.SERVER_DEV ? ":20101" : "";
	public final static String SERVER_CONTEXT = BuildConfig.SERVER_DEV ? "/qrpay/" : "/app/qrpay/";

/////////////////////////////////////////////////////////////////
// Http Connecter 코드

	public final static boolean HTTP_PARAMETER_ACCEPT_JSON = false;		// Http 통신시 파라메터방식(UrlEncord, Json)

	public final static int SERVER_TIME_OUT = 15000;
	public final static String SERVER_TYPE_GET = "GET";
	public final static String SERVER_TYPE_POST = "POST";

	public final static String SERVER_ACCEPT_TYPE_JSON = "text/html,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	public final static String SERVER_ACCEPT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded;cahrset=UTF-8";
    public final static String SERVER_ACCEPT_MULTIPART_TYPE = "multipart/form-data";
	public final static String SERVER_ACCEPT_MULTIPART_TYPE_BOUNDARY = "multipart/form-data; boundary=";
	public final static String SERVER_ACCEPT_CHARSET = "windows-949,UTF-8;q=0.7,*;q=0.3";
	public final static String SERVER_CACHE_STATE = "no-cache";
	public final static String SERVER_CONNECTION_KEEP = "Keep-Alive";

	public final static String SERVER_CHARSET = "UTF-8";
	public final static String FILE_PATH_KEY = "FILE_PATH";

/////////////////////////////////////////////////////////////////
// Paging
    public final static int DEFAULT_PAGE_NO = 1;
    public final static int DEFAULT_PAGE_LODING_ITEM_COUNT = 50;

/*************************************************************
** IF RESPONSE CALLBACK KEY
*************************************************************/

	public final static int IF_RESID_APP_INTRO = 0x0010;
	public final static int IF_RESID_APP_DEVICE_UPDATE = 0x0020;
	public final static int IF_RESID_FAKE_LOG = 0x0030;

	public final static int IF_RESID_COMMON_ERROR= 0xf001;


/*************************************************************
** IF SERVICE NAME
*************************************************************/

	public final static String IF_SERID_APP_INTRO = "AppIntro.do";
	public final static String IF_SERID_APP_DEVICE_UPDATE = "AppDeviceUpdate.do";
	public final static String IF_SERID_FAKE_LOG = "AppDtctLog.do";
}
