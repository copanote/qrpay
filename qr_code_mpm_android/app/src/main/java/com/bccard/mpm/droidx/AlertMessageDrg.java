/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : AlertMessageDrg
 * 작성자명   : 20170448
 * 상세설명   : DroidX 에서 사용하는 파일
 * 적용범위   : mpm
 * 작성일자   : 2018.03.19
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

package com.bccard.mpm.droidx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import net.nshc.droidx3.manager.library.DroidXLibraryManager;

public class AlertMessageDrg extends Activity {
	public String msg= "";
	Activity mActivity;
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		mActivity = this;

		showAlertMessage(getIntent().getStringExtra("msg"));
	}

	public boolean showAlertMessage(String msg) {

		AlertDialog.Builder alert_internet_status = new AlertDialog.Builder(mActivity);
		alert_internet_status.setTitle("Droid-X 알림");
		alert_internet_status.setMessage(msg);
		alert_internet_status.setCancelable(false);
		alert_internet_status.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				DroidXLibraryManager.getInstance().stopService();
				mActivity.moveTaskToBack(true);
				mActivity.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		alert_internet_status.setPositiveButton("종료", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DroidXLibraryManager.getInstance().stopService();
				mActivity.moveTaskToBack(true);
				mActivity.finish();
	            android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		dialog = alert_internet_status.show();
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialog!=null && dialog.isShowing()){
			dialog.dismiss();
		}
	}
}