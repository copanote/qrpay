/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : PushIntentService
 * 작성자명   : 20170448
 * 상세설명   : Push, Polling 후 받은 데이터를 가공하는 Service
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

package com.bccard.mpm.push;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.bccard.mpm.IntroActivity;
import com.bccard.mpm.R;
import com.bccard.mpm.common.Constant;
import com.bccard.mpm.network.bean.BeanResPushData;
import com.bccard.mpm.ui.main.MainActivity;
import com.bccard.mpm.util.LogHelper;
import com.bccard.mpm.util.UtilHelper;

public class PushIntentService extends IntentService {
	private static final String TAG = PushIntentService.class.getSimpleName();

	public PushIntentService() {
		super(TAG);
	}

	@Override
	public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		LogHelper.e("IntentService : Start");

		Bundle bundle = intent.getExtras();

		BeanResPushData fcmMessage = (BeanResPushData) bundle.get(Constant.EXTRA_PUSH_LIST_KEY);

		if (fcmMessage != null) {
			LogHelper.e("Push FcmMessage : " + fcmMessage);

			if ((fcmMessage.getTITLE() != null && !fcmMessage.getTITLE().isEmpty()) && (fcmMessage.getMSG() != null && !fcmMessage.getMSG().isEmpty())) {
				if (UtilHelper.isRunningProcess(this)) {

					// Main 으로 이동 후
					if (MainActivity.MAIN_WEB_VIEW_URL != null && !MainActivity.MAIN_WEB_VIEW_URL.isEmpty()) {
						Intent intentActivityData = new Intent();
						intentActivityData.setAction(Constant.ACTION_PUSH_INTENT_SERVICE_RESPONSE);
						intentActivityData.setPackage(getApplicationContext().getPackageName()); //안드로이드 8.0+ 보안 정책 강화 영향 앱 패키지 명시 필요
						intentActivityData.addCategory(Intent.CATEGORY_DEFAULT);
						intentActivityData.putExtra(Constant.EXTRA_PUSH_LIST_KEY, fcmMessage);
						sendBroadcast(intentActivityData);
					} else {
						showNotification(fcmMessage);
						LogHelper.e("PUSH >>> Not After Call Main");
					}
				} else {
					showNotification(fcmMessage);
					LogHelper.e("PUSH >>> isRunningProcess : false");
				}
			} else {
				LogHelper.e("PUSH >>> fcmMessage getPUSH_TITLE or getPUSH_MSG : isEmpty");
			}
		} else {
			LogHelper.e("PUSH >>> fcmMessage fcmMessage : Null");
		}

		LogHelper.e("IntentService : End");
	}

	public void showNotification(BeanResPushData pushItem) {
		Intent intentOpen = null;

		intentOpen = new Intent(this, IntroActivity.class);
		intentOpen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intentOpen.putExtra(Constant.EXTRA_PUSH_NOTI_CLICK_VALUE, pushItem);

		/*
		if (UtilHelper.isRunningProcess(this)) {
			intentOpen = new Intent(this, MainActivity.class);
			intentOpen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intentOpen.putExtra(Constant.EXTRA_PUSH_NOTI_CLICK_VALUE, pushItem);
		} else {
			intentOpen = new Intent(this, IntroActivity.class);
			intentOpen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intentOpen.putExtra(Constant.EXTRA_PUSH_NOTI_CLICK_VALUE, pushItem);
		}
		*/

		// target 31+ FCM
		// Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE be specified when creating a PendingIntent.
		// Strongly consider using FLAG_IMMUTABLE, only use FLAG_MUTABLE if some functionality depends on the PendingIntent being mutable,
		// e.g. if it needs to be used with inline replies or bubbles.
		PendingIntent pendingIntent = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			pendingIntent = PendingIntent.getActivity(this, 0, intentOpen, PendingIntent.FLAG_IMMUTABLE);
		} else {
			pendingIntent = PendingIntent.getActivity(this, 0, intentOpen, PendingIntent.FLAG_UPDATE_CURRENT);
		}

		String CHANNEL_ID = "mpm_push";// The id of the channel.

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			int notifyID = 1;
			CharSequence name = "mpm";// The user-visible name of the channel.
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

			notificationManager.createNotificationChannel(channel);
		}
		Notification.Builder builder = new Notification.Builder(this);
//		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),android.R.mipmap.ic_launcher));	//큰이미지
		builder.setSmallIcon(R.mipmap.ic_launcher);																	//큰 이미지 밑에 작은이미지
		builder.setTicker(pushItem.getTITLE());																		//알람 발생시 잠깐 나오는 텍스트
		builder.setContentTitle(pushItem.getTITLE());																//제목
		builder.setContentText(pushItem.getMSG());																	//내용
		builder.setWhen(System.currentTimeMillis());																//알람시간
		builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);								//알람 발생시 액션
		builder.setContentIntent(pendingIntent);																	//알람 눌렸을시 실행할 작업
		builder.setAutoCancel(true);																				//알람 눌렸을시 자동으로 삭제 여부
		builder.setNumber(0);																						//확인하지 않을 알림 갯수를 설정
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			builder.setShowWhen(true);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			builder.setChannelId(CHANNEL_ID);
		}

		PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
		wakelock.acquire(2000);

		builder.setPriority(Notification.PRIORITY_MAX);			// 잠깐동안 알림이 상단에 뜸
		notificationManager.notify(0, builder.build());

		if(wakelock.isHeld()) {
			wakelock.release();
		}
	}

	public void showToast(String message) {
		final String msg = message;
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}
}
