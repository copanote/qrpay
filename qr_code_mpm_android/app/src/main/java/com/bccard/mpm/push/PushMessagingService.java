/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : PushMessagingService
 * 작성자명   : 20170448
 * 상세설명   : FCM 를 통해 들어오는 Push Service
 * 적용범위   : mpm
 * 작성일자   : 2018.01.23
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

import android.content.Intent;

import androidx.annotation.NonNull;

import com.bccard.mpm.common.Constant;
import com.bccard.mpm.common.UserInfo;
import com.bccard.mpm.network.bean.BeanResPushData;
import com.bccard.mpm.util.LogHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class PushMessagingService extends FirebaseMessagingService {
	@Override
	public void onNewToken(@NonNull String token) {
		super.onNewToken(token);
		LogHelper.e("Refreshed Token : " + token);
		UserInfo.getInstance(this).setPushToken(token);
	}

	@Override
	public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
		if (remoteMessage.getData().size() > 0) {
			LogHelper.e("Message Data : " + remoteMessage.getData());

			remoteMessage.getData();
			BeanResPushData fcmMessage = new BeanResPushData();
			fcmMessage.setSEQ(getPushMapData(remoteMessage.getData(), "SEQ"));
			fcmMessage.setTITLE(getPushMapData(remoteMessage.getData(), "TITLE"));
			fcmMessage.setMSG(getPushMapData(remoteMessage.getData(), "MSG"));
			fcmMessage.setVALUE(getPushMapData(remoteMessage.getData(), "VALUE"));

			Intent service = new Intent(this, PushIntentService.class);
			service.putExtra(Constant.EXTRA_PUSH_LIST_KEY, fcmMessage);
			this.startService(service);
		}
	}

	private String getPushMapData(Map<String, String> pushData, String key) {
		if (pushData.containsKey(key)) {
			return pushData.get(key);
		} else {
			return "";
		}
	}
}