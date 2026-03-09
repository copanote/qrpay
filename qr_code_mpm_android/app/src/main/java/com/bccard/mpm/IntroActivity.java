/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : IntroActivity
 * 작성자명   : 20170448
 * 상세설명   : IntroActivity
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

package com.bccard.mpm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.bccard.mpm.common.BaseActivity;
import com.bccard.mpm.common.Constant;
import com.bccard.mpm.common.UserInfo;
import com.bccard.mpm.droidx.DroidXServiceListener;
import com.bccard.mpm.network.IServerCallback;
import com.bccard.mpm.network.ServerConnecter;
import com.bccard.mpm.network.ServerInfo;
import com.bccard.mpm.network.bean.BeanResAppIntro;
import com.bccard.mpm.network.bean.BeanResPushData;
import com.bccard.mpm.ui.common.PermissionActivity;
import com.bccard.mpm.ui.main.MainActivity;
import com.bccard.mpm.util.LogHelper;
import com.bccard.mpm.util.UtilHelper;
import com.google.gson.Gson;

import net.nshc.droidx3.manager.library.DroidXLibraryManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IntroActivity extends BaseActivity {

    public TextView tvIntroInfo = null;
    public static void checksum1(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checksum1();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }
        // minSDKVersion이 21이므로 더이상 아래 분기문 불필요함.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#E43F49"));
//        }
        setContentView(R.layout.act_intro);

        /*
        아래의 이유로 주석처리함.
        1. 알림 권한을 받아야 fcm 이 동작함.
        2. task로 async로 수행되는 프로세스를 onCreate에서 수행하기 부적절.
         */
//        String token = "";
//        try{
//            token = FirebaseMessaging.getInstance().getToken().getResult();
//            LogHelper.w("fcm token : " + token);
//
//        }catch (IllegalStateException ise){
//            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
//                @Override
//                public void onSuccess(String s) {
//                    LogHelper.w("fcm token : " + s);
//                }
//            });
//        }

        /* UUID , FCM 토큰등 정보등이. 현재 권한동의받지 않은 현재 초기프로세스에서 수행하기에 부적잘하며,
        개발초기가 아닌이상, 현재 프로세스에 아래의 정보들을 로그로 확인할 필요가 없어보이므로 모두 주석처리함.
        차기 업데이트시에는 삭제 해야 함.
        */
//        LogHelper.w("Uuid : " + UserInfo.getUUID(mContext));
//        LogHelper.w("getPushToken : " + UserInfo.getPushToken());
//        LogHelper.w("getDeviceName : " + UserInfo.getDeviceName());
//        LogHelper.w("getModelCode : " + UserInfo.getModelCode());
//        LogHelper.w("getOsVersion : " + UserInfo.getOsVersion());
//        LogHelper.w("getOsName : " + UserInfo.getOsName());
//        LogHelper.w("getBrandName : " + UserInfo.getBrandName());
//        LogHelper.w("isRunningProcess : " + UtilHelper.isRunningProcess(mContext));
//        LogHelper.w("getFrontActivity : " + UtilHelper.getFrontActivity(mContext));
//        LogHelper.w("getFrontActName : " + UtilHelper.getFrontActName(mContext));
//        LogHelper.w(LogHelper.G_TAG, "Server Url : " + ServerInfo.SERVER_URL + ServerInfo.SERVER_PORT);
//        LogHelper.w("====================================================");

        if (Constant.DETECT_FINISH_APP) {
//            reqFakeLog();
        } else {
            initControl();

            boolean isPermissionAgreed;
            ArrayList<String> checkPermission = new ArrayList<>();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                // sdk 33 이상
                //  POST_NOTIFICATIONS : FCM 푸시 토큰을 위해 필요한 권한.
                checkPermission.add(Manifest.permission.POST_NOTIFICATIONS);
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // sdk 29 이상
                //  MANAGE_EXTERNAL_STORAGE권한을 삭제하고 Media api로 변경으로 인해 필요한 권한 없음
//                isPermissionAgreed = Environment.isExternalStorageManager() &&
//                        UtilHelper.permissionCheck(mContext, checkPermission);
            }else {
                // sdk 28 이하
                //  WRITE_EXTERNAL_STORAGE 권한 필요
                checkPermission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            isPermissionAgreed = UtilHelper.permissionCheck(mContext, checkPermission);

            if (isPermissionAgreed) {
                if (UtilHelper.isConntectNetWork(mContext)) {
                    initDroidX();
                    moveMainActivity();

//                    reqAppVersionCheck();
                } else {
                    tvIntroInfo.setText(getString(R.string.msg_not_connect_network));
                    networkErrorActivity(getString(R.string.msg_not_connect_network));
                }
            } else {
                movePermissionActivity();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ivLoading.post(new Runnable() {
//            @Override
//            public void run() {
//                frameAnimation.start();
//            }
//        });
    }

    @Override
    protected void onPause() {
//        ivLoading.post(new Runnable() {
//            @Override
//            public void run() {
//                frameAnimation.stop();
//            }
//        });
        super.onPause();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogHelper.w("IntroActivity onActivityResult");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (Environment.isExternalStorageManager()) {
//                if (UtilHelper.isConntectNetWork(mContext)) {
//                    initDroidX();
//                    reqAppVersionCheck();
//                } else {
//                    tvIntroInfo.setText(getString(R.string.msg_not_connect_network));
//                    networkErrorActivity(getString(R.string.msg_not_connect_network));
//                }
//            }else{
//                finish();
//            }
//        }
        if (requestCode == Constant.REQAC_PERMISSION_INTRO) {
            if (resultCode == RESULT_OK) {
                if (UtilHelper.isConntectNetWork(mContext)) {
                    initDroidX();
                    //TODO
//                    reqAppVersionCheck();
                    moveMainActivity();
                } else {
                    tvIntroInfo.setText(getString(R.string.msg_not_connect_network));
                    networkErrorActivity(getString(R.string.msg_not_connect_network));
                }
            } else {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Droid-X 서비스 종료
        if(mDroidXLibraryManager != null) {
            mDroidXLibraryManager.stopService();
        } else {
            DroidXLibraryManager.getInstance(getApplicationContext()).stopService();
        }
        super.onBackPressed();
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Init
    private DroidXLibraryManager mDroidXLibraryManager = null;

    public void initDroidX() {
        if (Constant.DROID_X_RUN) {
            LogHelper.e("IntroActivity initDroidX");

            // Droid-X 동작 결과를 받을 CallbackListener
            DroidXServiceListener serviceListener = new DroidXServiceListener(this);

            // Droid-X Manager 객체 획득
            mDroidXLibraryManager = DroidXLibraryManager.getInstance(getApplicationContext());
            // Notification 등록
            mDroidXLibraryManager.setNotificationUse(false);
            // Log 표시 설정
            mDroidXLibraryManager.setLogView(true);
            // Intro 팝업 표시 설정
            mDroidXLibraryManager.setIntroView(false);
            // 업데이트 최대 시간 설정 (기본: 5000ms)
            mDroidXLibraryManager.setUpdateMaxTime(5000);
            // 기본 삭제창 이용 여부 설정
            mDroidXLibraryManager.setDefaultRemoveDialogMode(true);
            // SDCard를 포함한 스토리지 관련 접근을 사용할 것인지 결정(false일 경우 관련권한 미체크)
//            mDroidXLibraryManager.setUseStorage(false);
            // CallbackListener 등록
            mDroidXLibraryManager.setDXCallbackListener(serviceListener);
            // Droid-X 서비스 이전 실행여부 체크 및 종료
            if (mDroidXLibraryManager.chkProc()) mDroidXLibraryManager.stopService();
            // Droid-X 실행
            boolean isDroidXRun = mDroidXLibraryManager.startService();
            if (!isDroidXRun) {
                                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.deleteNotificationChannel(Constant.DROIDX_CHANNEL_ID);
                }
                notificationManager.cancel(Constant.DROIDX_NOTI_ID);

                showToast("Droid-X 실행에 실패하였습니다.");
            }
            // Droid-X 버전 확인
            mDroidXLibraryManager.getDroidXVersion(0);
        }
    }


    public void initControl() {
        tvIntroInfo = findViewById(R.id.tv_intro_info);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Connect Server

    /**
     * App Intro 서비스 호출
     */
    public void reqAppVersionCheck() {
        tvIntroInfo.setText(getString(R.string.msg_version_info_check));

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("DEVI_TYPE", ServerInfo.DEVI_TYPE);    // 앱 타입(A:안드로이드, I:IOS)
            jsonData.put("APP_VERSION", UtilHelper.getVersionName(mContext));   // 앱 버전
        } catch (JSONException e) {
            LogHelper.printException(e);
        }

        // Service 호출
        ServerConnecter conn = new ServerConnecter(mContext, mServerCallbackListener);
        //TODO AppIntro
        //conn.requestHttpPost(ServerInfo.IF_RESID_APP_INTRO, ServerInfo.IF_SERID_APP_INTRO, jsonData);
    }

    /**
     * App Intro 서비스 응답
     */
    public void resAppVersionCheck(String resultData) {
        BeanResAppIntro versionData = new BeanResAppIntro();
        try {
            JSONObject jsonVersion = new JSONObject(resultData);
            versionData = new Gson().fromJson(UtilHelper.getJsonData(jsonVersion,"version"), BeanResAppIntro.class);
        } catch (JSONException e) {
            LogHelper.printException(e);
        }

        if ("Y".equals(versionData.getUPDATE_YN())) {                 // 버전 업데이트 Flag 확인
            tvIntroInfo.setText(getString(R.string.msg_is_update));

            final String updateUrl = versionData.getAPP_URL();

            if ("Y".equals(versionData.getFORCE_UPDATE_YN())) {         // 강제 업데이트 Flag 확인
                new AlertDialog.Builder(mContext)
                        .setMessage(R.string.msg_update_force)
                        .setPositiveButton(R.string.btn_move, (dialog, whichButton) -> moveUpdateWebPage(updateUrl))
                        .create()
                        .show();
            } else {
                new AlertDialog.Builder(mContext)
                        .setMessage(R.string.msg_update)
                        .setPositiveButton(R.string.btn_update, (dialog, whichButton) -> moveUpdateWebPage(updateUrl))
                        .setNegativeButton(R.string.btn_cancel, (dialog, whichButton) -> moveMainActivity())
                        .create()
                        .show();
            }
        } else {
            moveMainActivity();
        }
    }

    /**
     * App FakeLog 서비스 호출
     */
    public void reqFakeLog() {
        UserInfo userInfo = UserInfo.getInstance(mContext);
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("DEVI_TYPE", ServerInfo.DEVI_TYPE);    // 앱 타입(A:안드로이드, I:IOS)
            jsonData.put("DEVI_OS_VER", userInfo.getOsVersion());
            jsonData.put("DEVI_MODL_NM", userInfo.getBrandName() + " " + userInfo.getModelCode());
            jsonData.put("APP_NM", mContext.getString(R.string.app_name));
            jsonData.put("APP_VER", UtilHelper.getVersionName(mContext));
            jsonData.put("DTCT_TP_CD", Constant.DETECT_CODE_FAKE);

            LogHelper.e("DEVI_OS_VER : " + userInfo.getOsVersion());
            LogHelper.e("DEVI_MODL_NM : " + userInfo.getBrandName() + " " + userInfo.getModelCode());
            LogHelper.e("APP_VER : " + UtilHelper.getVersionName(mContext));
        } catch (JSONException e) {
            LogHelper.printException(e);
        }

        // Service 호출
        ServerConnecter conn = new ServerConnecter(mContext, mServerCallbackListener);
        //conn.requestHttpPost(ServerInfo.IF_RESID_FAKE_LOG, ServerInfo.IF_SERID_FAKE_LOG, jsonData);
    }

    /**
     * App FakeLog 서비스 응답
     */
    public void resFakeLog(String resultData) {
        LogHelper.e("resFakeLog : " + resultData);
        Toast.makeText(mContext, R.string.msg_fake, Toast.LENGTH_SHORT).show();
        finish();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Connect Handle

    /**
     * Response 응답 받는 Callback 함수
     */
    @SuppressLint("SetTextI18n")
    private final IServerCallback mServerCallbackListener = (callbackRspNo, responseData) -> {
        if (mIsActivityDestroy) {
            return;
        }

        LogHelper.e("getSuccess : " + responseData.getSuccess());
        if (responseData.getSuccess()) {
            switch (callbackRspNo) {
                case ServerInfo.IF_RESID_APP_INTRO:
                    resAppVersionCheck(responseData.getMsg());
                    break;
                case ServerInfo.IF_RESID_FAKE_LOG:
                    resFakeLog(responseData.getMsg());
                    break;
                default:
                    break;
            }
        } else {
            switch (callbackRspNo) {
                case ServerInfo.IF_RESID_APP_INTRO:
                    break;
                case ServerInfo.IF_RESID_FAKE_LOG:
                    resFakeLog(responseData.getMsg());
                    break;
                default:
                    break;
            }

            if (callbackRspNo != ServerInfo.IF_RESID_FAKE_LOG) {
                if (!ServerInfo.RES_ERROR_CODE.equals(responseData.getCode())) {
                    tvIntroInfo.setText(getString(R.string.msg_base_info_fail) + " (" + responseData.getCode() + ")");
                } else {
                    tvIntroInfo.setText(getString(R.string.msg_base_info_fail) + " (" + ServerInfo.RES_ERROR_CODE + ")");
                }
            }
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////

    // 권한체크 화면이동
    public void movePermissionActivity() {
        Intent intent = new Intent(IntroActivity.this, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(!isRemoteDialogShowing()){
            startActivityForResult(intent, Constant.REQAC_PERMISSION_INTRO);
        }
    }

    // 메인 화면 이동
    public void moveMainActivity() {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intentValue = getIntent();
        if (intentValue != null){
            if (intentValue.hasExtra(Constant.EXTRA_PUSH_NOTI_CLICK_VALUE)) {
                intent.putExtra(Constant.EXTRA_PUSH_NOTI_CLICK_VALUE, (BeanResPushData)intentValue.getExtras().get(Constant.EXTRA_PUSH_NOTI_CLICK_VALUE));
            }
        }
        if(!isRemoteDialogShowing()){
            startActivity(intent);
            finish();
        }
    }

    // 업데이트 페이지로 이동
    public void moveUpdateWebPage(String url) {
        LogHelper.w("Update Page : " + url);
        long now = System.currentTimeMillis();

        if (url != null && !url.isEmpty()) {
            if (url.contains(Constant.DOMAIN) && !url.contains("?")) {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + "?param=" + now));
                if(!isRemoteDialogShowing()) {
                    startActivity(intent);
                    finish();
                }
            } else {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if(!isRemoteDialogShowing()) {
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    // Network 미연결시 확인후 종료
    public void networkErrorActivity(String message) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_ok, (dialog, whichButton) -> finish())
                .create()
                .show();
    }
}
