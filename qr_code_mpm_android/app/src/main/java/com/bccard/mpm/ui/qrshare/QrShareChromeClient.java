/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : QrShareChromeClient
 * 작성자명   : 20170448
 * 상세설명   : QrSharePopupActivity 연동 WebChromeClient 클래스
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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class QrShareChromeClient extends WebChromeClient {
    private Context mContext = null;

	// WebPage를 이용한 파일업로드 관련
	public ValueCallback<Uri> mUploadMessage = null;
	public ValueCallback<Uri[]> mFilePathCallback = null;
	public final static int FILECHOOSER_JELLY_BEAN_RESULTCODE = 1;
	public final static int FILECHOOSER_RESULTCODE = 2;


	QrShareChromeClient(Context context)    {
		super();
		mContext = context;    
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
	}
	@Override
	public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		new AlertDialog.Builder(view.getContext())
				.setMessage(message)
				.setPositiveButton(android.R.string.ok,
						new AlertDialog.OnClickListener(){
							public void onClick(DialogInterface dialog, int which) {
								result.confirm();
							}
						})
				.setCancelable(false)
				.create()
				.show();
		return true;
//		return super.onJsAlert(view, url, message, result);
	}
	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
		new AlertDialog.Builder(view.getContext())
				.setMessage(message)
				.setPositiveButton(android.R.string.ok,
						new AlertDialog.OnClickListener(){
							public void onClick(DialogInterface dialog, int which) {
								result.confirm();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new AlertDialog.OnClickListener(){
							public void onClick(DialogInterface dialog, int which) {
								result.cancel();
							}
						})
				.setCancelable(false)
				.create()
				.show();
		return true;
//		return super.onJsConfirm(view, url, message, result);
	}

//////////////////////////////////////////////////////////////////////////////////////////
// WebPage를 이용한 파일업로드 관련
	// For Android 4.1+
	@SuppressWarnings("unused")
	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
		mUploadMessage = uploadMsg;

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");
		((Activity)mContext).startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_JELLY_BEAN_RESULTCODE);
	}

	// For Android 5.0+
	public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
		if (mFilePathCallback != null) {
			mFilePathCallback.onReceiveValue(null);
			mFilePathCallback = null;
		}
		mFilePathCallback = filePathCallback;

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		((Activity)mContext).startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);

		return true;
	}
}

