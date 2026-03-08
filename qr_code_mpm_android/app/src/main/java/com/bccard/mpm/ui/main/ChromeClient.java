/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : ChromeClient
 * 작성자명   : 20170448
 * 상세설명   : MainActivity 연동 WebChromeClient 클래스
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bccard.mpm.common.BaseActivity;
import com.bccard.mpm.util.LogHelper;

public class ChromeClient extends WebChromeClient {
    private Context mContext = null;
    private Handler mCallbackMain = null;
    private WebView mMainWebview = null;

	// WebPage를 이용한 파일업로드 관련
	public ValueCallback<Uri> mUploadMessage = null;
	public ValueCallback<Uri[]> mFilePathCallback = null;
	public final static int FILECHOOSER_JELLY_BEAN_RESULTCODE = 1;
	public final static int FILECHOOSER_RESULTCODE = 2;


	ChromeClient(Context context, WebView webview)    {
		super();
		mContext = context;
		mMainWebview = webview;
	}

	@Override
	public boolean onCreateWindow(final WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
		LogHelper.e("resultMsg.obj : " + resultMsg.obj);
		WebView newWebView = new WebView(mContext);
		view.addView(newWebView);
		WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
		transport.setWebView(newWebView);
		resultMsg.sendToTarget();

		newWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW);
				browserIntent.setData(Uri.parse(url));
				if (mContext instanceof BaseActivity){
					if(!((BaseActivity)mContext).isRemoteDialogShowing()){
						mContext.startActivity(browserIntent);
					}
				}else{
					mContext.startActivity(browserIntent);
				}
				return true;
			}
		});

		return true;
	}

	public void setCallbackMain(Handler callbackMain) {this.mCallbackMain = callbackMain;}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
//        LogHelper.d("Loading Web : " + newProgress);
		super.onProgressChanged(view, newProgress);
	}
	@Override
	public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		new AlertDialog.Builder(view.getContext())
//				.setTitle(R.string.title_info)
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
//				.setTitle(R.string.title_info)
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
		if (mContext instanceof BaseActivity){
			if(!((BaseActivity)mContext).isRemoteDialogShowing()){
				((Activity)mContext).startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_JELLY_BEAN_RESULTCODE);
			}
		}else{
			((Activity)mContext).startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_JELLY_BEAN_RESULTCODE);
		}
	}

	// For Android 5.0+
	public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
		if (mFilePathCallback != null) {
			mFilePathCallback.onReceiveValue(null);
			mFilePathCallback = null;
		}
		mFilePathCallback = filePathCallback;

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		if (mContext instanceof BaseActivity){
			if(!((BaseActivity)mContext).isRemoteDialogShowing()){
				((Activity)mContext).startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
			}
		}else{
			((Activity)mContext).startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
		}
		return true;
	}
}

