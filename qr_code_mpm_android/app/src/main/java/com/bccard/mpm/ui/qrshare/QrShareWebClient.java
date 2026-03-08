/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : QrShareWebClient
 * 작성자명   : 20170448
 * 상세설명   : QrSharePopupActivity 연동 WebViewClient 클래스
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

package com.bccard.mpm.ui.qrshare;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bccard.mpm.R;
import com.bccard.mpm.common.Constant;
import com.bccard.mpm.util.LogHelper;


public class QrShareWebClient extends WebViewClient {
    private Context mContext = null;
    private Handler mCallbackQrShare = null;

    public QrShareWebClient(Context context) {
        super();
        this.mContext = context;
    }

    public void setCallbackQrShare(Handler callbackQrShare) {this.mCallbackQrShare = callbackQrShare;}

	private void sendMessage(int key, Object message) {
		Message callbackMessage = new Message();
		callbackMessage.what = key;
		callbackMessage.obj = message;

		mCallbackQrShare.sendMessage(callbackMessage);
	}

    @Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		//return super.shouldOverrideUrlLoading(view, url);

		view.loadUrl(url);
		return true;
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
		LogHelper.i("QrShare Load Cookie : " + cookie);

		sendMessage(Constant.WM_WV_ONLOAD, "QrShareWebClient : onPageFinished");
        super.onPageFinished(view, url);
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
		sendMessage(Constant.WM_WV_DISMISS_LOADING,"");
		LogHelper.e( "QrShare onReceivedError : " + errorCode );
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
