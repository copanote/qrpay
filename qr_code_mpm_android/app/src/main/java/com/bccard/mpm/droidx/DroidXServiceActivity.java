/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : DroidXServiceActivity
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
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.bccard.mpm.IntroActivity;

import net.nshc.droidx3.manager.library.DroidXLibraryManager;

public class DroidXServiceActivity extends Activity {
	
	private Context context;
	
	private DroidXLibraryManager mDroidXLibraryManager = null;
	private com.bccard.mpm.droidx.DroidXServiceListener mServiceTestListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.sample_activity_droidxrunner);
		
		this.context = getApplicationContext();
		// Droid-X 동작 결과를 받을 CallbackListener
		mServiceTestListener = new com.bccard.mpm.droidx.DroidXServiceListener(this);
		
		// Droid-X Manager 객체 획득
		mDroidXLibraryManager = DroidXLibraryManager.getInstance(context);
		// Notification 등록
		mDroidXLibraryManager.setNotificationUse(true);
		// Log 표시 설정
		mDroidXLibraryManager.setLogView(true);
		// Intro 팝업 표시 설정
		mDroidXLibraryManager.setIntroView(true);
		// 업데이트 최대 시간 설정 (기본: 5000ms)
		mDroidXLibraryManager.setUpdateMaxTime(5000);
		// 업데이트 범위 설정(기본: 전체)
//		mDroidXLibraryManager.setUpdateMode(0);
		// 기본 삭제창 이용 여부 설정
		mDroidXLibraryManager.setDefaultRemoveDialogMode(false);
		// SDCard를 포함한 스토리지 관련 접근을 사용할 것인지 결정(false일 경우 관련 권한 미체크)
		// runSDCard() 메서드를 사용 하지 않는다면 필요 x
//		mDroidXLibraryManager.setUseStorage(false);
		// CallbackListener 등록
		mDroidXLibraryManager.setDXCallbackListener(mServiceTestListener);
		// Droid-X 서비스 이전 실행여부 체크 및 종료
		if(mDroidXLibraryManager.chkProc())mDroidXLibraryManager.stopService();
		// Droid-X 실행
		mDroidXLibraryManager.startService();
		// Droid-X 버전 확인
		mDroidXLibraryManager.getDroidXVersion(0);
		
		Intent intent = new Intent(DroidXServiceActivity.this, IntroActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		
		this.finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	// 명시적인 서비스 종료: 이 엑티비티를 종료하면 서비스가 올바르게 
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mDroidXLibraryManager.stopService();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
