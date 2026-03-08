/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : DroidXServiceHandler
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
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bccard.mpm.IntroActivity;

public class DroidXServiceHandler extends Handler {
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		if (msg.obj instanceof TextView) {
			String buf = msg.arg2 + "%";
			((TextView) msg.obj).setText(buf);
		} else if (msg.obj instanceof ProgressBar) {
			((ProgressBar) msg.obj).setProgress(msg.arg2);
		} else if (msg.obj instanceof ImageView) {
			((ImageView) msg.obj).setImageResource(msg.arg2);
		} else if (msg.obj instanceof Activity) {
			// 다음 액티비티 실행(HelloAcitivity class)
			Intent intent = new Intent((Activity) msg.obj, IntroActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			((Activity) msg.obj).startActivity(intent);
			((Activity) msg.obj).finish();
		}
	}
}
