/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : SharedPrefHelper
 * 작성자명   : 20170448
 * 상세설명   : SharedPreference 관련 Helper 클래스
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

package com.bccard.mpm.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.bccard.mpm.common.Constant;

public class SharedPrefHelper {

	public static void setSharedMpmData(Context context, String key, String value) {
		SharedPreferences pref = context.getSharedPreferences(Constant.PREF_MPM_INFO, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

	    editor.putString(key, value);
	    editor.commit();
	}
	
	public static String getSharedMpmData(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(Constant.PREF_MPM_INFO, Activity.MODE_PRIVATE);
		String mpmData = pref.getString(key, null);

		if( mpmData == null ) {
			return "";
		}
		
		return mpmData;
	}

	public static void removeSharedLoginFlag(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(Constant.PREF_MPM_INFO, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		editor.remove(key);
		editor.commit();
	}	
}
