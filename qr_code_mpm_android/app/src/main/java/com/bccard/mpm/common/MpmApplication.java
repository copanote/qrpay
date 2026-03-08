/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : MpmApplication
 * 작성자명   : 20170448
 * 상세설명   : Application 클래스
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

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bccard.mpm.util.LogHelper;

import net.nshc.droidx3.addon.DroidXAddOnListener;
import net.nshc.droidx3.addon.DroidXAddOnManager;

public class MpmApplication extends Application {

	private int activityReferences = 0;
	private boolean isActivityChangingConfigurations = false;
	private DroidXAddOnManager addOnManager;
	private int resultCode = 0;
	private boolean isForeground = false;
	private final Handler handler = new Handler(Looper.getMainLooper());
	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (isForeground && resultCode > 0) {
				// 원격 제어 탐지 메서드
				addOnManager.runRemoteCheck(getApplicationContext());
			} else if (resultCode == 0) {
				// 아직 초기화되지 않았을 시, 0.5초 후 재실행
				handler.postDelayed(runnable, 500);
			}
		}
	};
	@Override
	public void onCreate() {
		super.onCreate();
		Constant.FINISH_APP = false;
		Constant.DETECT_FINISH_APP = false;
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {}

			@Override
			public void onActivityResumed(@NonNull Activity activity) {}

			@Override
			public void onActivityPaused(@NonNull Activity activity) {}

			@Override
			public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {}

			@Override
			public void onActivityDestroyed(@NonNull Activity activity) {}

			@Override
			public void onActivityStarted(Activity activity) {
				// Activity가 시작될 때 호출
				if (++activityReferences == 1 && !isActivityChangingConfigurations) {
					// 애플리케이션 포그라운드 전환
					isForeground = true;
					handler.post(runnable);
				}
			}

			@Override
			public void onActivityStopped(Activity activity) {
				// 구성 변경으로 인한 액티비티 재생성 구분
				isActivityChangingConfigurations = activity.isChangingConfigurations();

				if (--activityReferences == 0 && !isActivityChangingConfigurations) {
					// 애플리케이션 백그라운드 전환
					isForeground = false;
					handler.removeCallbacks(runnable);
				}
			}
		});

		addOnManager = new DroidXAddOnManager(new DroidXAddOnListener() {
			@Override
			public void onCompleteInit(int initResultCode) {
				resultCode = initResultCode;

				if (initResultCode < 0) {
					// 초기화 실패
				}
			}

			@Override
			public void onCheckedConnection(String connectionResultCode) {
				if (!connectionResultCode.startsWith("*")) {
					// 탐지되었을 시
					if (isForeground) {
						handler.removeCallbacks(runnable);
//						showAlert("Result", "탐지!!\n실제 리턴 값:" + connectionResultCode);
						Intent intent = new Intent(Constant.LOCAL_EVENT_DROIDX_DETECTED_REMOTE_APP);
						intent.putExtra("package",connectionResultCode);
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					}
				} else {
					// 탐지되지 않을 시, 10초 후 실행
					handler.postDelayed(runnable, 10000);
				}
			}
		});

		// 초기화 실행
		addOnManager.initialize(getApplicationContext());
	}

//	@Override
//	protected void attachBaseContext(Context context) {
//		super.attachBaseContext(context);
//		MultiDex.install(this);
//	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public static void fake() {
		LogHelper.e("Fake ======================> 위변조 탐지");
		Constant.DETECT_FINISH_APP = true;
	}

	public static void nonFake() {
		LogHelper.e("nonFake ======================> 위변조 미탐지");
		Constant.DETECT_FINISH_APP = false;
	}
}
