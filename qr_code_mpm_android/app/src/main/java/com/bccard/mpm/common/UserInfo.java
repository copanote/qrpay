/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : UserInfo
 * 작성자명   : 20170448
 * 상세설명   : 사용자 정보 클래스
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

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.bccard.mpm.util.LogHelper;
import com.bccard.mpm.util.SharedPrefHelper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UserInfo {

	private final Context context;

	private static UserInfo sInstance = null;

	private UserInfo(Context context) {
		this.context = context;
	}

	public static UserInfo getInstance(Context context)
	{
		if(sInstance == null)
		{
			sInstance = new UserInfo(context);
		}

		return sInstance;
	}

	private String uuid = "";
	private String pushToken = "";
	private final String deviceName = Build.DEVICE;
	private final String modelCode = Build.MODEL;
	private final String osVersion = Build.VERSION.RELEASE;
	private final String osName = Build.ID;
	private final String brandName = Build.MANUFACTURER;

	public String getUuid() {
		if (uuid == null || uuid.isEmpty()) {
			uuid = SharedPrefHelper.getSharedMpmData(context, Constant.PREF_MPM_KEY_UUID);

			if (uuid.isEmpty()) {
				uuid = getComplexUuid(context);
				SharedPrefHelper.setSharedMpmData(context, Constant.PREF_MPM_KEY_UUID, uuid);
			}
		}
		return uuid;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getModelCode() {
		return modelCode;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public String getOsName() {
		return osName;
	}

	public String getBrandName() {
		return brandName;
	}

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	private String getComplexUuid(Context context) {
		String addKey = "" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10;
		LogHelper.i("addKey : " + addKey);
		String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		LogHelper.i("androidId : " + androidId);
		String uuidKey = androidId + addKey;
		LogHelper.i("uuidKey : " + uuidKey);
		UUID deviceUuid = null;
		if (!"9774d56d682e549c".equals(androidId)) {
            deviceUuid = UUID.nameUUIDFromBytes(uuidKey.getBytes(StandardCharsets.UTF_8));
            LogHelper.i("deviceUuid : " + deviceUuid);
        } else {
			deviceUuid = UUID.randomUUID();
			LogHelper.i("else randomUUID deviceUuid : " + deviceUuid);
		}
		LogHelper.i("ret UUID : " + deviceUuid);
		return deviceUuid.toString();
	}
}
