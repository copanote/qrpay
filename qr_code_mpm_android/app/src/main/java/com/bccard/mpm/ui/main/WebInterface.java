/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : WebInterface
 * 작성자명   : 20170448
 * 상세설명   : MainActivity 연동 Web Bridge 클래스
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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.bccard.mpm.common.BaseActivity;
import com.bccard.mpm.common.Constant;
import com.bccard.mpm.util.LogHelper;
import com.bccard.mpm.util.SharedPrefHelper;

import java.util.HashMap;
import java.util.Map;

public class WebInterface {
	private Context mContext = null;
	private Handler mCallbackMain = null;
	
	WebInterface(Context context)    {
		super();
		mContext = context;
	}
	
	public void setCallbackMain(Handler callbackMain) {this.mCallbackMain = callbackMain;}

	private void sendMessage(int key, Object message) {
		Message callbackMessage = new Message();
		callbackMessage.what = key;
		callbackMessage.obj = message;

		mCallbackMain.sendMessage(callbackMessage);
	}

	@JavascriptInterface
	public void showNFilterKeypad(String mode, String name, String len, String desc) {
		Map<String,String> filterOption = new HashMap<String,String>();
		filterOption.put("mode", mode);
		filterOption.put("name", name);
		filterOption.put("len", len);
		filterOption.put("desc", desc);

		sendMessage(Constant.WM_WV_SHOW_NFILTER_KEYBOARD, filterOption);
	}

	@JavascriptInterface
	public void hideNFilterKeypad() {
		sendMessage(Constant.WM_WV_HIDE_NFILTER_KEYBOARD, "");
	}

	@JavascriptInterface
	public void nfilter(String publicKey) {
		sendMessage(Constant.WM_WV_SET_NFILTER_PUBLIC_KEY, publicKey);
	}

	@JavascriptInterface
	public void getDevice() {
		sendMessage(Constant.WM_WV_GET_DEVICE, "");
	}

	@JavascriptInterface
	public void qrLoad(String qr, String merNm) {
		Map<String,String> qrLoad = new HashMap<String,String>();
		qrLoad.put("qr", qr);
		qrLoad.put("merNm", merNm);

		sendMessage(Constant.WM_WV_QR_LOAD, qrLoad);
	}

	@JavascriptInterface
	public void qrShare(String url) {
		sendMessage(Constant.WM_WV_QR_SHARE, url);
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

    @JavascriptInterface
	public void goPolicyTreatment() {
		if (mContext instanceof BaseActivity){
			if(!((BaseActivity)mContext).isRemoteDialogShowing()){
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PRIVACY_POLICY_TREATMENT_URL));
				mContext.startActivity(intent);
			}
		}else{
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PRIVACY_POLICY_TREATMENT_URL));
			mContext.startActivity(intent);
		}
	}

	@JavascriptInterface
	public void qrClear() {
		SharedPrefHelper.removeSharedLoginFlag(mContext, Constant.PREF_MPM_KEY_QR);
	}

	@JavascriptInterface
	public void getDecalCode() {
		sendMessage(Constant.WM_WV_GET_DECAL_CODE, "");
	}

    @JavascriptInterface
	public void getTrnsData() {
		sendMessage(Constant.WM_WV_GET_TRNS_DATA, "");
	}

	@JavascriptInterface
	public void showLoading(String type) {
		sendMessage(Constant.WM_WV_SHOW_LOADING, type);
	}

	@JavascriptInterface
	public void dismissLoading() {
		sendMessage(Constant.WM_WV_DISMISS_LOADING, "");
	}

	@JavascriptInterface
	public void linkExtraBrowser(String url) {
		if (mContext instanceof BaseActivity){
			if(!((BaseActivity)mContext).isRemoteDialogShowing()){
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				mContext.startActivity(intent);
			}
		}else{
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			mContext.startActivity(intent);
		}
	}
}
