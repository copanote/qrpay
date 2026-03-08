/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : BaseActivity
 * 작성자명   : 20170448
 * 상세설명   : Activity 기본상속 클래스
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bccard.mpm.R;

public class BaseActivity extends AppCompatActivity {
	protected Context mContext = null;
	protected boolean mIsActivityDestroy = false;
	protected AppCompatDialog mLoadingDialog = null;

	private AlertDialog remoteCheckDialog = null;
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constant.LOCAL_EVENT_DROIDX_DETECTED_REMOTE_APP.equals(intent.getAction())){
				String packageName = intent.getStringExtra("package");
				PackageManager pm = getApplicationContext().getPackageManager();
				ApplicationInfo ai;
				try {
					ai = pm.getApplicationInfo( packageName, 0);
				} catch (final PackageManager.NameNotFoundException e) {
					ai = null;
				}
				final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : packageName);
				try{
					remoteCheckDialog = new AlertDialog.Builder(BaseActivity.this)
							.setMessage("[ "+applicationName+getString(R.string.dialog_scan_remote_app))
							.setCancelable(false)
							.setPositiveButton(R.string.btn_finish, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int whichButton){
									finish();
								}
							})
							.setCancelable(false)
							.create();
					remoteCheckDialog.show();
				} catch (Exception e){
				}
			}else if (Constant.LOCAL_EVENT_DROIDX_FINISH_APP.equals(intent.getAction())){
				finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocalBroadcastManager.getInstance(BaseActivity.this).registerReceiver(mMessageReceiver,
				new IntentFilter(Constant.LOCAL_EVENT_DROIDX_DETECTED_REMOTE_APP));
		LocalBroadcastManager.getInstance(BaseActivity.this).registerReceiver(mMessageReceiver,
				new IntentFilter(Constant.LOCAL_EVENT_DROIDX_FINISH_APP));
		mContext = BaseActivity.this;
		mIsActivityDestroy = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Constant.FINISH_APP) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		mIsActivityDestroy = true;
		super.onDestroy();
	}

	protected void showToast(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(int msgId) {
		Toast.makeText(mContext, getResources().getString(msgId), Toast.LENGTH_SHORT).show();
	}

	/**
	 * 일반 로딩뷰 띄우기
	 * @param type
	 */
	protected void showLoadingDialog(int type) {

		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			dismissLoadingDialog();
		} else {

			mLoadingDialog = new AppCompatDialog(BaseActivity.this, R.style.dialog_activity_style);
			mLoadingDialog.setCancelable(false);
			mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			mLoadingDialog.setContentView(R.layout.dialog_loading);
			mLoadingDialog.show();

		}


		final ImageView ivLoading = (ImageView) mLoadingDialog.findViewById(R.id.iv_loading);
		final AnimationDrawable frameAnimation = (AnimationDrawable) ivLoading.getBackground();
		ivLoading.post(new Runnable() {
			@Override
			public void run() {
				frameAnimation.start();
			}
		});

		TextView tvMsg = (TextView) mLoadingDialog.findViewById(R.id.tv_msg);
		if (type==Constant.LOADING_TYPE_GENERAL){
			tvMsg.setVisibility(View.GONE);

		}else if (type==Constant.LOADING_TYPE_SECRET){
		}else{
			tvMsg.setVisibility(View.GONE);
		}
	}

	/**
	 * 로딩뷰 취소
	 */
	protected void dismissLoadingDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	public boolean isRemoteDialogShowing(){
		if(remoteCheckDialog!=null && remoteCheckDialog.isShowing()){
			return true;
		}
		return false;
	}
}