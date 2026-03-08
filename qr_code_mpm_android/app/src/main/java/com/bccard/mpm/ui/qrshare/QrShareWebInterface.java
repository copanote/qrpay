/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : QrShareWebInterface
 * 작성자명   : 20170448
 * 상세설명   : QrSharePopupActivity 연동 Web Bridge 클래스
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
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.bccard.mpm.common.Constant;
import com.bccard.mpm.util.LogHelper;

public class QrShareWebInterface {
	private Context mContext = null;
	private Handler mCallbackQrShare = null;
	
	QrShareWebInterface(Context context)    {
		super();
		mContext = context;
	}
	
	public void setCallbackQrShare(Handler callbackQrShare) {this.mCallbackQrShare = callbackQrShare;}

	private void sendMessage(int key, Object message) {
		Message callbackMessage = new Message();
		callbackMessage.what = key;
		callbackMessage.obj = message;

		mCallbackQrShare.sendMessage(callbackMessage);
	}

	@JavascriptInterface
	public void logout(String message) {
		sendMessage(Constant.WM_WV_LOGOUT, message);
	}

	@JavascriptInterface
	public void showToast(String message) {
		Toast.makeText( mContext, "WEB : " + message, Toast.LENGTH_SHORT).show();
	}

    @JavascriptInterface
    public void printLog(String logMessage) {
        LogHelper.e( "WEB : " + logMessage );
    }
}
