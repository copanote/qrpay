/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : DroidXServiceListener
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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bccard.mpm.common.Constant;
import com.bccard.mpm.util.LogHelper;

import net.nshc.droidx3.engine.ScanResult;
import net.nshc.droidx3.manager.DroidXCallbackListenerV2;
import net.nshc.droidx3.manager.library.DroidXLibraryManager;
import net.nshc.droidx3.provide.Const;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DroidXServiceListener implements DroidXCallbackListenerV2 {
	private final Context mContext;
	// 샘플의 메인 화면에서
	public static boolean modeIntro = true;
	public static boolean modeRootingScan = false;

	public static boolean isAPIRunning = false;

	/**
	 * DroidX서비스의 흐름을 도와주는 리스너
	 * @param ctx 진행이 끝난 뒤 다음화면으로 진행하기 위해 현재 Activity를 필요로함
	 */
	public DroidXServiceListener(Context ctx) {
		mContext = ctx;
	}

	/**
	 * startService() 초기화 완료시 최초로 실행되는 콜백
	 * @param resultCode
	 *        0: 초기화 성공
	 *	      1~15 : 퍼미션 부족 관련 오류(APPENDIX 6 항목 참고)
	 *        100: so파일 무결성 검증 실패, 이후 진행되지 않으므로 종료 필요
	 *        101: 기존에 설치된 패턴/엔진이 임의로 변조 되었음
	 *        102: 최초 엔진 설치시 문제가 발생함, 이후 진행되지 않으므로 종료 필요
	 *             해당 오류의 경우 라이브러리 적용시에 엔진/패턴 파일이 누락된 경우 발생 할 수 있으므로 확인이 필요함.
	 */
	@Override
	public void callbackInit(int resultCode) {
		LogHelper.i("NSHC_Listener", "callbackInit: " + resultCode);

		if(resultCode == 0) {
			if (modeIntro) DroidXLibraryManager.getInstance().runUpdate();
		} else {
			showWarningDialog("Droid-X 서비스를 시작하기위한 환경이 적절하지 않습니다.\n\n코드 : "+resultCode);
		}
		isAPIRunning = false;
	}

	/**
	 * runUpdate() 동작 완료시 실행
	 * @param resultCode
	 *        0: 정상
	 *        1: Offline으로 실패
	 *        2: 정책파일 파싱 실패
	 *        3: 복호화 실패
	 *        4: 파일 없음
	 *        5: 입출력 실패
	 *        6: 전자서명 실패
	 *        7: 엔진 업데이트 실패
	 *        8: 최대시간 초과
	 */
	@Override
	public void callbackUpdate(int resultCode) {
		LogHelper.i("NSHC_Listener", "callbackUpdate: " + resultCode);

		if (modeIntro){
			DroidXLibraryManager.getInstance().runRootingCheck();
		} else {
			Toast t = Toast.makeText(mContext, "Update code: " +resultCode, Toast.LENGTH_LONG);
			t.show();
		}
		isAPIRunning = false;
	}

//	@Override
	public void callbackRoot(boolean resultCode) {
		//Deprecated
	}
	
	/**
	 * @param resultCode
	 ***** runRootingCheck() 동작 완료시 실행
	 *        -1: 알려지지 않은 모든 에러
	 *        -2: 패턴의 무결성 검증 실패
	 *        0: 정상
	 *        1: su 파일 탐지
	 *        2: 루팅 관련 애플리케이션 탐지
	 *        3: 기타 루팅 방법 탐지
	 *
	 ***** runRootingScan() 동작 완료시 실행
	 *        resultCode & Const.FLAG_ROOTING_SCAN_DETECTED
	 *          0: 정상
	 *          Const.FLAG_ROOTING_SCAN_DETECTED: 루팅 관련 파일 탐지
	 */
	@Override
	public void callbackRoot(int resultCode) {
		LogHelper.i("NSHC_Listener", "callbackRoot(int): " + resultCode + "// "+(resultCode & Const.FLAG_ROOTING_SCAN_DETECTED ));

		if (resultCode < 0) {
			showWarningDialog("검사 진행 중 오류가 발생 하였습니다.\n\n코드 : "+resultCode);
		} else{
			// runRootingCheck() 를 사용한 경우
			if (modeIntro) {
				if (resultCode == 0) {
					// 정상(루팅되지 않음)
					// 설치된 앱만 검사
//					DroidXLibraryManager.getInstance().runMalwareScan();
					// 내부 저장소의 파일과 설치된 앱을 모두 검사 하는 API, 보안의 완벽을 원하시면 아래 API의 사용을 권장합니다
					// 파일이 많을 경우 검사 시간이 조금더 길어 질 수 있습니다.
				} else {
					// 루팅
					showWarningDialog("단말에서 루팅이 탐지되었습니다.\n\n코드 : "+resultCode);
				}
			}
			else {
				if (modeRootingScan) {  // 메인에서 runRootingScan()을 실행한 경우.
					// runRootingScan() 을 사용하였을 경우 아래와 같이 (resultCode & Const.FLAG_ROOTING_SCAN_DETECTED)의 결과값으로 루팅을 판단하여야 합니다.
					Toast.makeText(mContext.getApplicationContext(), "루팅 탐지결과:" + (resultCode & Const.FLAG_ROOTING_SCAN_DETECTED ), Toast.LENGTH_SHORT).show();
				} else {
					// resultCode 값에 대한 것은 가이드를 확인해 주세요.
					Toast.makeText(mContext.getApplicationContext(), "루팅 탐지결과:" + resultCode, Toast.LENGTH_SHORT).show();
				}
			}
		}
		// 설치된 앱만 검사 (캐시기능을 이용하여 설치된 멀웨어 검사 실행을 빠르게 수행함, 캐싱이므로 최초 검사는 포함되지 않음)
		DroidXLibraryManager.getInstance().runFastMalwareScan();
		// 일반 설치된 앱만 검사
		// DroidXLibraryManager.getInstance().runMalwareScan();
		// 내부 저장소의 파일과 설치된 앱을 모두 검사 하는 API, 완전한 탐색을 원하시면 아래 API의 사용을 권장합니다.
		// 파일이 많을 경우 검사 시간이 길어 질 수 있습니다.
		// DroidXLibraryManager.getInstance().runSDCardScan();
		isAPIRunning = false;
	}

//	@Override
	public void callbackEngineVersion(String[] localVersion, String[] serverVersion) {
		LogHelper.i("NSHC_Listener", "callbackEngineVersion: " + localVersion[0]+" | "+serverVersion[0]);
		isAPIRunning = false;
	}

	// setDefaultRemoveDialogMode를 false로 설정했을 경우에는 아래의 메소드로 리턴됩니다.
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void callbackMalwareResult(int type, int iResult,  Map mapResult) {
		LogHelper.i("NSHC_Listener","callbackMalwareResult: "+type + " | "+iResult + " | "+mapResult.size());

		if (iResult < 0) {
			showWarningDialog("검사 진행 중 오류가 발생 하였습니다.\n\n코드 : "+iResult);
			return;
		}

		if(mapResult.size() > 0) { // 악성코드 탐지 결과 하나 이상 발견되었을 경우
			Bundle mBundle = new Bundle();
			for(Entry<String, ScanResult> sre : ((HashMap<String, ScanResult>)mapResult).entrySet()) {
				ScanResult sr = sre.getValue();
				mBundle.putSerializable(sre.getKey(), sr);

				LogHelper.d("NSHC_Listener", "-callbackMalwareResult : " +sr.getTargetPath() + " | "+sr.getPackageName() + " | "+sr.getResultCode());
			}

			Intent mIntent = new Intent(mContext.getApplicationContext(), DroidXServiceListDel.class);
			mIntent.putExtra("DX_TotalSize", iResult);
			mIntent.putExtra("DX_Malwares", mBundle);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.getApplicationContext().startActivity(mIntent);

		} else { // 악성코드 탐지 결과 하나도 없을 경우
			LogHelper.i("NSHC_Listener", "Virus is nothing.");
		}
		isAPIRunning = false;
	}

	@Override
	public void measureUpdateEngine(String bytesize) {
		LogHelper.d("TEST","업데이트 진행용량 - "+bytesize);
	}

	/**
	 * runMalwareScan()을 포함한 스캔관련 동작 진행 중 진행률을 주기적으로 알림
	 * @param percent 진행률 0 - 100까지 표현
	 * @param currentFilepath 현재 진행되고있는 파일의 경로
	 */
	@Override
	public void updateProgress(int percent, String currentFilepath) {
		LogHelper.i("NSHC_Listener", "updateProgress: " + currentFilepath + " | "+percent);
	}

	/**
	 * setDefaultRemoveDialogMode(true) 일때 runMalwareScan() 동작 완료시 실행
	 * @param resultCode
	 *        -1: 알려지지 않은 모든 에러
	 *        -2: 패턴의 무결성 검증 실패
	 *        0: 정상
	 *        1이상: 발견된 악성코드의 수
	 */
	@Override
	public void callbackMalware(int resultCode) {
		LogHelper.i("NSHC_Listener", "callbackMalware: " + resultCode);
		if (resultCode < 0) {
			showWarningDialog("검사 진행 중 오류가 발생 하였습니다.\n\n코드 : "+resultCode);
			return;
		}
		if ( resultCode > 0) {
			Intent intent = new Intent(Constant.LOCAL_EVENT_DROIDX_FINISH_APP);
			LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(intent);
		}
	}

	/**
	 * setDefaultRemoveDialogMode(true) 일때 실시간 감시에 의한 스캔 동작 완료시 실행
	 * @param resultCode
	 *        -1: 알려지지 않은 모든 에러
	 *        -2: 패턴의 무결성 검증 실패
	 *        0: 정상
	 *        1이상: 발견된 악성코드의 수
	 */
	@Override
	public void callbackRealTimeMalware(int resultCode) {
		LogHelper.i("NSHC_Listener", "callbackRealTimeMalware: " + resultCode);
		isAPIRunning = false;
	}

	@Deprecated
	@Override
	public void callbackDetailMalware(int resultCode) {
		//Deprecated
	}
	
	/**
	 * 
	 * @param message
	 */
	private void showWarningDialog(String message) {
		Bundle bun = new Bundle();
		bun.putString("msg", message);

		Intent popupIntent = new Intent(mContext, AlertMessageDrg.class);

		popupIntent.putExtras(bun);
		// Android 31 PendingIntent Issue
		PendingIntent pie = null;

		//보안이슈로 FLAG_IMMUTABLE SDK 23 부터 셋팅 필수
		int flags = PendingIntent.FLAG_ONE_SHOT;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			flags |= PendingIntent.FLAG_IMMUTABLE;
		}
		pie = PendingIntent.getActivity(mContext, 0, popupIntent, flags);

		try {
			pie.send();
		} catch (PendingIntent.CanceledException e) {
			LogHelper.i("NSHC_Listener", e.getMessage());
		}
	}

}
