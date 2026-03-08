/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : PushInstanceIdService
 * 작성자명   : 20170448
 * 상세설명   : FCM 를 Token ID 갱신시 호출되는 Service
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

import com.bccard.mpm.common.UserInfo;
import com.bccard.mpm.util.LogHelper;

//public class PushInstanceIdService extends FirebaseInstanceIdService {
//	@Override
//	public void onTokenRefresh() {
//		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//		LogHelper.e("Refreshed token: " + refreshedToken);
//
//		UserInfo.setPushToken(refreshedToken);
//	}
//}