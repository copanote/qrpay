/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : WebClient
 * 작성자명   : 20170448
 * 상세설명   : MainActivity 연동 WebViewClient 클래스
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

package com.bccard.mpm.ui.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bccard.mpm.R;
import com.bccard.mpm.common.BaseActivity;
import com.bccard.mpm.common.Constant;
import com.bccard.mpm.util.LogHelper;

import java.net.URISyntaxException;


public class WebClient extends WebViewClient {

	public static final String INTENT_URI_START = "intent:";
	public static final String INTENT_URI_MARKET = "market:";
	public static final String INTENT_URI_MARKET_WEB = "https://play.google.com/store/apps/details?id=";
	public static final String INTENT_FALLBACK_URL = "browser_fallback_url";
	public static final String URI_SCHEME_MARKET = "market://details?id=";
	public static final String INTENT_TEL = "tel:";
	public static final String INTENT_MAILTO = "mailto:";
	public static final String INTENT_SMS = "sms:";

    private Context mContext = null;
    private Handler mCallbackMain = null;

    public WebClient(Context context) {
        super();
        this.mContext = context;
    }

    public void setCallbackMain(Handler callbackMain) {this.mCallbackMain = callbackMain;}

	private void sendMessage(int key, Object message) {
		Message callbackMessage = new Message();
		callbackMessage.what = key;
		callbackMessage.obj = message;

		mCallbackMain.sendMessage(callbackMessage);
	}

    @Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		//return super.shouldOverrideUrlLoading(view, url);
		LogHelper.w("view Id : " + view.getId());
		LogHelper.w("UrlLoading : " + url);

		if (url.toLowerCase().startsWith(INTENT_URI_START) || url.toLowerCase().startsWith(INTENT_URI_MARKET)) {
			Intent parsedIntent = null;
			try {
				parsedIntent = Intent.parseUri(url, 0);
				if (mContext!=null){
					if (mContext instanceof BaseActivity){
						if(!((BaseActivity)mContext).isRemoteDialogShowing()){
							mContext.startActivity(parsedIntent);
						}
					}else{
						mContext.startActivity(parsedIntent);
					}
				}
			} catch (ActivityNotFoundException | URISyntaxException e) {
				return doFallback(view, parsedIntent);
			}
		} else if (url.toLowerCase().startsWith(INTENT_URI_MARKET_WEB)) {
			url.replace(INTENT_URI_MARKET_WEB, URI_SCHEME_MARKET);
			Intent parsedIntent = null;
			try {
				parsedIntent = Intent.parseUri(url, 0);
				if (mContext!=null) {
					if (mContext instanceof BaseActivity){
						if(!((BaseActivity)mContext).isRemoteDialogShowing()){
							mContext.startActivity(parsedIntent);
						}
					}else{
						mContext.startActivity(parsedIntent);
					}
				}
			} catch (ActivityNotFoundException | URISyntaxException e) {
				return doFallback(view, parsedIntent);
			}
		} else if (url.toLowerCase().startsWith(INTENT_TEL)) {
			try {
				if (mContext!=null) {
					if (mContext instanceof BaseActivity){
						if(!((BaseActivity)mContext).isRemoteDialogShowing()){
							mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
						}
					}else{
						mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
					}
				}
			} catch (ActivityNotFoundException e) {

			}
		}else if (url.startsWith(INTENT_MAILTO)) {
			try {
				if (mContext!=null) {
					if (mContext instanceof BaseActivity){
						if(!((BaseActivity)mContext).isRemoteDialogShowing()){
							mContext.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
						}
					}else{
						mContext.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
					}
				}
			} catch (ActivityNotFoundException e) {

			}
		}else if (url.startsWith(INTENT_SMS)) {
			try {
				if (mContext!=null) {
					if (mContext instanceof BaseActivity){
						if(!((BaseActivity)mContext).isRemoteDialogShowing()){
							mContext.startActivity(new Intent(Intent.ACTION_SEND, Uri.parse(url)));
						}
					}else{
						mContext.startActivity(new Intent(Intent.ACTION_SEND, Uri.parse(url)));
					}
				}
			} catch (ActivityNotFoundException e) {

			}
		} else {
			view.loadUrl(url);
		}
		return true;
	}

	public boolean doFallback(WebView view, Intent parsedIntent) {
		if (parsedIntent == null) {
			return false;
		}

		String fallbackUrl = parsedIntent.getStringExtra(INTENT_FALLBACK_URL);
		if (fallbackUrl != null) {
			view.loadUrl(fallbackUrl);
			return true;
		}

		String packageName = parsedIntent.getPackage();
		if (packageName != null) {
			if (mContext instanceof BaseActivity){
				if(!((BaseActivity)mContext).isRemoteDialogShowing()){
					mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_SCHEME_MARKET + packageName)));
				}
			}else{
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_SCHEME_MARKET + packageName)));
			}
			return true;
		}
		return false;
	}
	@Override
	public void onPageFinished(WebView view, String url) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			//noinspection deprecation
			CookieSyncManager.getInstance().sync();
		} else {
			CookieManager.getInstance().flush();
		}

		String cookie = CookieManager.getInstance().getCookie(url);
		LogHelper.i("Load Cookie : " + cookie);

		sendMessage(Constant.WM_WV_ONLOAD, "WebClient : onPageFinished");
        super.onPageFinished(view, url);
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);

		LogHelper.e( "onReceivedError : " + errorCode );
		LogHelper.e( "failingUrl : " + failingUrl );
		switch(errorCode) {
			case ERROR_AUTHENTICATION: break;               	// 서버에서 사용자 인증 실패
			case ERROR_BAD_URL: break;							// 잘못된 URL
			case ERROR_CONNECT: break;                          // 서버로 연결 실패
			case ERROR_FAILED_SSL_HANDSHAKE: break;    			// SSL handshake 수행 실패
			case ERROR_FILE: break;                             // 일반 파일 오류
			case ERROR_FILE_NOT_FOUND: break;               	// 파일을 찾을 수 없습니다
			case ERROR_HOST_LOOKUP:								// 서버 또는 프록시 호스트 이름 조회 실패
				sendMessage(Constant.WM_WV_ERROR_HOST_LOOKUP, mContext.getString(R.string.msg_not_connect_network));
				break;
			case ERROR_IO: break;                              	// 서버에서 읽거나 서버로 쓰기 실패
			case ERROR_PROXY_AUTHENTICATION: break;   			// 프록시에서 사용자 인증 실패
			case ERROR_REDIRECT_LOOP: break;               		// 너무 많은 리디렉션
			case ERROR_TIMEOUT: break;                          // 연결 시간 초과
			case ERROR_TOO_MANY_REQUESTS: break;     			// 페이지 로드중 너무 많은 요청 발생
			case ERROR_UNKNOWN: break;                        	// 일반 오류
			case ERROR_UNSUPPORTED_AUTH_SCHEME: break; 			// 지원되지 않는 인증 체계
			case ERROR_UNSUPPORTED_SCHEME: break;          		// URI가 지원되지 않는 방식
		}
	}
}
