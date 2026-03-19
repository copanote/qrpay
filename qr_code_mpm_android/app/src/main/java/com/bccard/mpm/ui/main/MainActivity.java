/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : MainActivity
 * 작성자명   : 20170448
 * 상세설명   : 메인 웹뷰를 가지는 MainActivity
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

package com.bccard.mpm.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.app.ActivityCompat;

import com.bccard.mpm.BuildConfig;
import com.bccard.mpm.R;
import com.bccard.mpm.common.BaseActivity;
import com.bccard.mpm.common.Constant;
import com.bccard.mpm.common.UserInfo;
import com.bccard.mpm.droidx.DroidXServiceListener;
import com.bccard.mpm.network.IServerCallback;
import com.bccard.mpm.network.ServerConnecter;
import com.bccard.mpm.network.ServerInfo;
import com.bccard.mpm.network.bean.BeanResPushData;
import com.bccard.mpm.network.bean.BeanResponseData;
import com.bccard.mpm.ui.common.ReceiptActivity;
import com.bccard.mpm.ui.qrreader.QRReaderActivity;
import com.bccard.mpm.ui.qrscan.QRScanActivity;
import com.bccard.mpm.ui.qrshare.QrSharePopupActivity;
import com.bccard.mpm.util.FileHelper;
import com.bccard.mpm.util.LogHelper;
import com.bccard.mpm.util.SharedPrefHelper;
import com.bccard.mpm.util.UtilHelper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nshc.nfilter.NFilter;
import com.nshc.nfilter.command.view.NFilterOnClickListener;
import com.nshc.nfilter.command.view.NFilterTO;
import com.nshc.nfilter.util.NFilterUtils;
import com.nshc.nfilter.util.SecurityHelper;

import net.nshc.droidx3.manager.library.DroidXLibraryManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {
    public static String MAIN_WEB_VIEW_URL = "";

    private Context mContext = null;
    private DroidXLibraryManager mDroidXLibraryManager = null;
    private DroidXServiceListener mServiceDroidListener = null;

    public View mRootView = null;
    private WebView mWvWebView = null;

    // nFilter
    private NFilter nfilter = null;

    // PushToken Update
    private boolean mSetDevicePushToken = false;
    private Handler mGetDeviceUpdateHandler = new Handler();
    private boolean mIsGetDeviceUpdateSuccess = true;
    private int mGetDeviceUpdateTryCount = 0;

    // Alert
    private final AlertDialog mPushAlertDialog = null;
    private ReceiptActivity mPushAlertDialogActivity = null;

    // IntentService
    private PushIntentServiceReceiver mPushReceiver;

    // Member
    private String mQrShareUrl = "";

    private WebClient mWebClient = null;
    private WebInterface mWebInterface = null;
    private ChromeClient mChromeClient = null;

    private final String mKeyword = "android";

    private String mPushMoveUrl = "";
//    private DroidXAddonService mDroidXAddonService = null;

    private UserInfo userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }
        if (!BuildConfig.SERVER_DEV){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);   // 캡쳐방지
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        setContentView(R.layout.act_main);
        mContext = MainActivity.this;

        String flag = SharedPrefHelper.getSharedMpmData(mContext,Constant.PREF_MPM_KEY_INSTALL_FLAG);

        // Droid-X
        mDroidXLibraryManager = DroidXLibraryManager.getInstance();
        userInfo = UserInfo.getInstance(mContext);
        // Push 데이터 처리
        Intent intentValue = getIntent();
        if (intentValue != null) {
            if (intentValue.hasExtra(Constant.EXTRA_PUSH_NOTI_CLICK_VALUE)) {
                BeanResPushData pushData = (BeanResPushData)intentValue.getExtras().get(Constant.EXTRA_PUSH_NOTI_CLICK_VALUE);
                LogHelper.e("Main getIntent msg : " + pushData.toString());

                String parsingUrl = UtilHelper.parsingPushMoveUrl(pushData.getVALUE());
                if (!parsingUrl.isEmpty()) {
                    mPushMoveUrl = Constant.WEB_VIEW_BASE_URL + parsingUrl;
                }
            }
        }

        // register Push BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(Constant.ACTION_PUSH_INTENT_SERVICE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        mPushReceiver = new PushIntentServiceReceiver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mPushReceiver, intentFilter, RECEIVER_NOT_EXPORTED
            );
        }else {
            registerReceiver(mPushReceiver, intentFilter);
        }

        // nFilter Init
        nfilter = new NFilter(this);
        nfilter.setNoPadding(true);
        nfilter.setPlainDataEnable(true);
        nfilter.registerReceiver();

        // Init
        initControl();

        // Web Url 이동
        if (mPushMoveUrl != null && !mPushMoveUrl.isEmpty()) {
            loadWebUrl(mPushMoveUrl);

            mPushMoveUrl = "";
        } else {
            String qr = SharedPrefHelper.getSharedMpmData(mContext,Constant.PREF_MPM_KEY_QR);
            String merNm = SharedPrefHelper.getSharedMpmData(mContext,Constant.PREF_MPM_KEY_MERNM);

            LogHelper.e("flag : " + flag);
            LogHelper.e("merNm : " + merNm);
            LogHelper.e("qr : " + qr);

            if ("Y".equals(flag)) {
                if ((qr != null && !qr.isEmpty()) && (merNm != null && !merNm.isEmpty())) {
                    loadWebUrl(Constant.SUB_MAIN_URL, qr, merNm);
                } else {
                    loadWebUrl(Constant.MAIN_URL);
                }
            } else {
                loadWebUrl(Constant.MAIN_URL);
            }
        }

        // 쿠키싱크
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.createInstance(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent!=null) {
            if (intent.hasExtra(Constant.EXTRA_PUSH_NOTI_CLICK_VALUE)) {
                BeanResPushData pushData = (BeanResPushData)intent.getExtras().get(Constant.EXTRA_PUSH_NOTI_CLICK_VALUE);
                LogHelper.e("Main getIntent msg : " + pushData.toString());

                String parsingUrl = UtilHelper.parsingPushMoveUrl(pushData.getVALUE());
                if (!parsingUrl.isEmpty()) {
                    loadWebUrl(Constant.WEB_VIEW_BASE_URL + parsingUrl);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        LogHelper.e("BackKey Url : " + mWvWebView.getUrl());

        // nFilter 키패드 열려있으면 닫음.
        if(hideNFilterKeypad()){
            return;
        }

        // 내부 URL 일 경우 약속된 형태로 그렇지 않으면 히스토리백
        String url = mWvWebView.getUrl();
        if (url.contains(Constant.DOMAIN)) {

            // 바닥 URL 일 경우 앱 종료
            if (url.contains(Constant.MAIN_URL)  || url.contains(Constant.LOGIN_URL) || url.contains(Constant.API_URL)) {
                finishActivity();
            } else {
                executeJavascript("back()");
            }
        } else {
            if (mWvWebView.canGoBack()) {
                mWvWebView.goBack();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogHelper.e("onResume");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.getInstance().startSync();
        }

        // Droid-X Manager 객체 획득
        if (mDroidXLibraryManager == null) {
            mDroidXLibraryManager = DroidXLibraryManager.getInstance(getApplicationContext());
        }

        if (mDroidXLibraryManager != null && !mDroidXLibraryManager.chkProc()) {
            initDroidX();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.getInstance().stopSync();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogHelper.e("onDestroy");

        // WEB_VIEW_URL init
        MAIN_WEB_VIEW_URL = "";

        // delete file
        FileHelper.deleteAllFile(getApplicationContext());

        // unregister BroadcastReceiver
        unregisterReceiver(mPushReceiver);

        // nFilter
        nfilter.unregisterReceiver();

        // Droid-X 서비스 종료
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.deleteNotificationChannel(Constant.DROIDX_CHANNEL_ID);
//        }
//        notificationManager.cancel(Constant.DROIDX_NOTI_ID);
        if(mDroidXLibraryManager != null) {
            mDroidXLibraryManager.stopService();
        } else {
            DroidXLibraryManager.getInstance(getApplicationContext()).stopService();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Constant.REQAC_QR_READER){
            String decalCode = "";
            if (data!=null){
                decalCode = data.getStringExtra(Constant.EXTRA_DECAL_CODE);
            }
            if (TextUtils.isEmpty(decalCode)){
                // 인텐트로 넘어온 값이 없을 경우 카메라로 스캔한 경우
                IntentResult result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, data);
                //check for null
                if (result != null) {
                    decalCode = result.getContents();
                }
            }
            JSONObject resultJson = new JSONObject();
            try {
                resultJson.put("DECAL_CODE", decalCode);
                LogHelper.e("DECAL_CODE : " + resultJson);
                executeJavascript("setDecalCode", resultJson);
            } catch (JSONException e) {
                LogHelper.e(e);
            }
        }
        else if(requestCode==Constant.REQAC_QR_SCAN){

            String trnsData = "";

            // 인텐트로 넘어온 값이 없을 경우 카메라로 스캔한 경우
            IntentResult result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, data);
            //check for null
            if (result != null) {
                trnsData = result.getContents();
            }
            if (!TextUtils.isEmpty(trnsData)){
                JSONObject resultJson = new JSONObject();
                try {
                    resultJson.put("TRNS_DATA", trnsData);
                    LogHelper.e("TRNS_DATA : " + resultJson);
                    executeJavascript("setTrnsData", resultJson);
                } catch (JSONException e) {
                    LogHelper.e(e);
                }
            }
        }
        else{
            if (resultCode == RESULT_OK) {
                switch (requestCode){
                    case Constant.REQAC_QR_SHARE:
                        if (data != null && data.hasExtra(Constant.EXTRA_QR_LOGOUT)) {
                            String message = data.getStringExtra(Constant.EXTRA_QR_LOGOUT);
                            pageLogout(message);
                        } else if (data != null && data.hasExtra(Constant.EXTRA_QR_ERROR)) {
                            String message = data.getStringExtra(Constant.EXTRA_QR_ERROR);
                            networkErrorActivity(message);
                        }
                        break;

                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==Constant.REQAC_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openQrShareActivity(mQrShareUrl);
            }
        }

        mQrShareUrl = "";
    }

    @Override
    public Object getSystemService(String name) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && !NFilterUtils.getInstance().isDexMode(this)){
            return SecurityHelper.getWrappedSystemService(super.getSystemService(name), name);
        } else{
            return super.getSystemService(name);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Init

    public void initDroidX() {
        if (Constant.DROID_X_RUN) {
            LogHelper.e("MainActivity initDroidX");
            // Droid-X 동작 결과를 받을 CallbackListener
            mServiceDroidListener = new DroidXServiceListener(this);

            // Notification 등록
            mDroidXLibraryManager.setNotificationUse(false);
            // Log 표시 설정
            mDroidXLibraryManager.setLogView(true);
            // Intro 팝업 표시 설정
            mDroidXLibraryManager.setIntroView(false);
            // 업데이트 최대 시간 설정 (기본: 5000ms)
            mDroidXLibraryManager.setUpdateMaxTime(5000);
            // 업데이트 범위 설정(기본: 전체)
//            mDroidXLibraryManager.setUpdateMode(0);
            // 기본 삭제창 이용 여부 설정
            mDroidXLibraryManager.setDefaultRemoveDialogMode(true);
            // SDCard를 포함한 스토리지 관련 접근을 사용할 것인지 결정(false일 경우 관련권한 미체크)
//            mDroidXLibraryManager.setUseStorage(false);
            // CallbackListener 등록
            mDroidXLibraryManager.setDXCallbackListener(mServiceDroidListener);
            // Droid-X 서비스 이전 실행여부 체크 및 종료
            if (mDroidXLibraryManager.chkProc()) mDroidXLibraryManager.stopService();
            // Droid-X 실행
//            setDroidNotification();
            boolean isDroidXRun = mDroidXLibraryManager.startService();

            if (!isDroidXRun) {
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    notificationManager.deleteNotificationChannel(Constant.DROIDX_CHANNEL_ID);
//                }
//                notificationManager.cancel(Constant.DROIDX_NOTI_ID);
                showToast("Droid-X 실행에 실패하였습니다.");
            }
            // Droid-X 버전 확인
            mDroidXLibraryManager.getDroidXVersion(0);
        }
    }

    // 기본 설정
    public void initControl() {
        // 웹뷰 셋팅
        mRootView = findViewById(R.id.lay_main_root);
        mWvWebView = findViewById(R.id.wv_main_view);
        mWvWebView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_UP){
                    hideNFilterKeypad();
                }

                return false;
            }
        });

        setWebview();

        // Root Layout Listener 등록
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect rectDisplayWindow = new Rect();
            private int lastDisplayWindowHeight = 0;

            @Override
            public void onGlobalLayout() {
                // 윈도우 내 보이는 rectangle 호출
                mRootView.getWindowVisibleDisplayFrame(rectDisplayWindow);
                final int visibleDecorViewHeight = rectDisplayWindow.height();

                if (lastDisplayWindowHeight != 0) {
                    if (lastDisplayWindowHeight > visibleDecorViewHeight + Constant.SYSTEM_KEYBOARD_DIFF) {
                        // keyboard show
                        hideNFilterKeypad();
                    } else if (lastDisplayWindowHeight + Constant.SYSTEM_KEYBOARD_DIFF < visibleDecorViewHeight) {
                        // keyboard hide
                    }
                }

                lastDisplayWindowHeight = visibleDecorViewHeight;
            }
        });
    }

    // 웹뷰 설정
    private void setWebview() {
        mWebClient = new WebClient(mContext);
        mWebClient.setCallbackMain(mWebEventCallbackHandler);
        mWebInterface = new WebInterface(mContext);
        mWebInterface.setCallbackMain(mWebEventCallbackHandler);
        mChromeClient = new ChromeClient(mContext, mWvWebView);
        mChromeClient.setCallbackMain(mWebEventCallbackHandler);

        mWvWebView.setWebViewClient(mWebClient);
        mWvWebView.getSettings().setJavaScriptEnabled(true);
        mWvWebView.getSettings().setSupportZoom(false);
        mWvWebView.getSettings().setBuiltInZoomControls(false);
        mWvWebView.getSettings().setDisplayZoomControls(false);
        mWvWebView.getSettings().setLoadWithOverviewMode(true);
        mWvWebView.getSettings().setUseWideViewPort(true);
        mWvWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWvWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWvWebView.getSettings().setSupportMultipleWindows(true);

//        mWvWebView.getSettings().setDatabaseEnabled(false);
        mWvWebView.getSettings().setDomStorageEnabled(true);

        mWvWebView.addJavascriptInterface(mWebInterface, mKeyword);
        mWvWebView.setWebChromeClient(mChromeClient);

        String userAgentString = mWvWebView.getSettings().getUserAgentString();
        mWvWebView.getSettings().setUserAgentString(userAgentString + " " + Constant.UASTRING +  UtilHelper.getVersionCode(mContext));

//        CookieManager.getInstance().removeSessionCookie();
//        CookieManager.getInstance().removeAllCookie();
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // Handler

    /**
     * WebEvent 응답 받는 Callback 함수
     */
    @SuppressLint("HandlerLeak")
    private final Handler mWebEventCallbackHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Constant.WM_WV_ONLOAD:        // 페이지 로딩 완료
                    String url = mWvWebView.getUrl();

                    if (url.contains(Constant.SMS_CERT_DOMAIN)) {
                        showTitle();
                    }else{
                        MAIN_WEB_VIEW_URL = mWvWebView.getUrl();
                        hideTitle();
                    }

                    String mpmAlak01 = getCookie(MAIN_WEB_VIEW_URL, "mpmalak01");

                    LogHelper.e("WM_WV_ONLOAD : " + url);
                    LogHelper.e("MAIN_WEB_VIEW_URL : " + MAIN_WEB_VIEW_URL);
                    LogHelper.e("mpmalak01 : " + mpmAlak01);

                    if (url.contains("about:blank")) {
                        loadWebUrl(MAIN_WEB_VIEW_URL);
                    }

                    /*
                    // 로그인 후 푸쉬로 받은 URL 이동
                    if (mpmAlak01 != null && !mpmAlak01.isEmpty()) {
                        if (mIsFirstStart && mPushMoveUrl != null && !mPushMoveUrl.isEmpty()) {
                            loadWebUrl(mPushMoveUrl);

                            mPushMoveUrl = "";
                        }
                    }

                    mIsFirstStart = false;
                    */
                    break;
                case Constant.WM_WV_SHOW_NFILTER_KEYBOARD:        // nFilter(키보드) 요청
                    Map<String,String> filterOption = (Map<String,String>) msg.obj;
                    String mode = filterOption.get("mode");
                    String name = filterOption.get("name");
                    String len = filterOption.get("len");
                    String desc = filterOption.get("desc");

                    LogHelper.e("WM_WV_SHOW_NFILTER_KEYBOARD : " + name);

                    executeJavascript("showNFilterKeypadCallBack", "", name, "");
                    showNFilterKeypad(mode, name, len, desc);
                    break;
                case Constant.WM_WV_HIDE_NFILTER_KEYBOARD:        // nFilter(키보드) 닫기
                    LogHelper.e("WM_WV_HIDE_NFILTER_KEYBOARD ");
                    executeJavascript("hideNFilterKeypadCallBack()");
                    hideNFilterKeypad();
                    break;
                case Constant.WM_WV_SET_NFILTER_PUBLIC_KEY:        // nFilter(키보드) Public Key 받음
                    String publicKey = (String) msg.obj;
                    LogHelper.e("WM_WV_SET_NFILTER_PUBLIC_KEY : " + publicKey);

                    nfilter.setPublicKey(publicKey);
                    break;
                case Constant.WM_WV_GET_DEVICE:        // Device 정보 전달
                    JSONObject jsonDeviceInfo = new JSONObject();
                    LogHelper.e("WM_WV_GET_DEVICE");

                    try {
                        jsonDeviceInfo.put("DEVI_TYPE", "A");
                        jsonDeviceInfo.put("MODL_NM", userInfo.getBrandName() + " " + userInfo.getModelCode());
                        jsonDeviceInfo.put("MOBIL_OS_NM", userInfo.getOsVersion());
                        jsonDeviceInfo.put("DEVI_ID", userInfo.getUuid());
                        jsonDeviceInfo.put("APP_VER", UtilHelper.getVersionName(mContext));

                        String pushToken = userInfo.getPushToken();
//                        if (pushToken != null && !pushToken.isEmpty()) {
//                            jsonDeviceInfo.put("DEVI_VAL", pushToken);
//                            mSetDevicePushToken = true;
//                        } else {
//                            jsonDeviceInfo.put("DEVI_VAL", "");
//                            mSetDevicePushToken = false;
//                        }
                        if ( TextUtils.isEmpty(pushToken)){
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                                try {
                                    if (!task.isSuccessful()) {
                                        LogHelper.w("Fetching FCM registration token failed", task.getException());
                                        jsonDeviceInfo.put("DEVI_VAL", "");
                                        mSetDevicePushToken = false;

                                    }else{
                                        String token = task.getResult();
                                        userInfo.setPushToken(token);
                                        jsonDeviceInfo.put("DEVI_VAL", token);
                                        mSetDevicePushToken = true;
                                    }
                                } catch (JSONException e) {
                                    LogHelper.w("JSONException", e.getMessage());
                                }
                                executeJavascript("setDevice", jsonDeviceInfo);
                            });
                        } else {
                            jsonDeviceInfo.put("DEVI_VAL", pushToken);
                            mSetDevicePushToken = true;
                            executeJavascript("setDevice", jsonDeviceInfo);
                        }

                    } catch (JSONException e) {
                        LogHelper.e(e);
                    }
                    break;
                case Constant.WM_WV_QR_LOAD:        // QR코드, 업체명 등록
                    Map<String,String> qrLoad = (Map<String,String>) msg.obj;
                    String qr = qrLoad.get("qr");
                    String merNm = qrLoad.get("merNm");
                    SharedPrefHelper.setSharedMpmData(mContext, Constant.PREF_MPM_KEY_QR, qr);
                    SharedPrefHelper.setSharedMpmData(mContext, Constant.PREF_MPM_KEY_MERNM, merNm);
                    LogHelper.e("WM_WV_QR_LOAD qr : " + qr + " || merNm : " + merNm);

                    SharedPrefHelper.setSharedMpmData(mContext,Constant.PREF_MPM_KEY_INSTALL_FLAG, "Y");
                    LogHelper.e("PREF_MPM_KEY_INSTALL_FLAG : Y");

                    // setDevice 에서 PushToken 키를 못 넘겼을경우 I/F를 통해 토큰키 전달
                    if (!mSetDevicePushToken) {
                        initGetDeviceUpdateHandler();
                    }
                    break;
                case Constant.WM_WV_QR_SHARE:        // QR코드 공유
                    mQrShareUrl = msg.obj.toString();
                    LogHelper.e("WM_WV_QR_SHARE : " + mQrShareUrl);

                    boolean isGrantStorage = grantExternalStoragePermission();
                    if (isGrantStorage) {
                        openQrShareActivity(mQrShareUrl);
                    }
                    break;
                case Constant.WM_WV_LOGOUT:
                    String message = (String) msg.obj;
                    LogHelper.e("WM_WV_LOGOUT : " + message);
                    mSetDevicePushToken = false;
                    pageLogout(message);
                    break;
                case Constant.WM_WV_ERROR_HOST_LOOKUP:
                    String messageNetwork = (String) msg.obj;
                    LogHelper.e("WM_WV_ERROR_HOST_LOOKUP : " + messageNetwork);

                    networkErrorActivity(messageNetwork);
                    break;
                case Constant.WM_WV_ERROR:
                    String messageError = (String) msg.obj;
                    LogHelper.e("WM_WV_ERROR : " + messageError);

                    networkErrorActivity(messageError);
                    break;
                case Constant.WM_WV_GET_DECAL_CODE:
                    openQrReaderActivity();
                    break;
                case Constant.WM_WV_GET_TRNS_DATA:
                    openQrScanActivity();
                    break;
                case Constant.WM_WV_SHOW_LOADING:
                    int type = Integer.parseInt((String) msg.obj);
                    showLoadingDialog(type);
                    break;
                case Constant.WM_WV_DISMISS_LOADING:
                    dismissLoadingDialog();
                    break;

                default:
                    break;
            }
        }
    };

    public void showTitle() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().show();
   }

    public void hideTitle() {
        getSupportActionBar().hide();
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Connect Server

    /**
     * App Device Update 서비스 호출
     */
    public void reqAppDeviceUpdate() {
        String Uuid = userInfo.getUuid();
        String pushToken = userInfo.getPushToken();

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("DEVI_TYPE", "A");                         // 디바이스 타입(A:안드로이드, I:아이폰)
            jsonData.put("DEVI_VAL", pushToken);                    // 푸시토큰
            jsonData.put("DEVI_ID", Uuid);                          // 기기 ID(UUID)
            jsonData.put("AFFI_CO_ID", Constant.UPDATE_DEVICE_AFFI_CO_ID);  // 제휴사 ID
        } catch (JSONException e) {
            LogHelper.printException(e);
        }
        // pushtoken이 빈값이 아닐 경우에만 이 프로세스로 진입하므로 필요없는 프로세스
//        if (TextUtils.isEmpty(pushToken)){
//            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
//                if (!task.isSuccessful()) {
//                    LogHelper.w("Fetching FCM registration token failed", task.getException());
//                }else{
//                    String newToken = task.getResult();
//                    userInfo.setPushToken(newToken);
//                    try {
//                        jsonData.put("DEVI_VAL", newToken);                    // 푸시토큰
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                    LogHelper.w("onComplete PUSH_TOKEN : " + pushToken);
//                    // Service 호출
//                    ServerConnecter conn = new ServerConnecter(mContext, mServerCallbackListener);
//                    conn.requestHttpPost(ServerInfo.IF_RESID_APP_DEVICE_UPDATE, ServerInfo.IF_SERID_APP_DEVICE_UPDATE, jsonData);
//                }
//            });
//        }
        // Service 호출
        ServerConnecter conn = new ServerConnecter(mContext, mServerCallbackListener);
        //conn.requestHttpPost(ServerInfo.IF_RESID_APP_DEVICE_UPDATE, ServerInfo.IF_SERID_APP_DEVICE_UPDATE, jsonData);
    }

    /**
     * App Device Update 서비스 응답
     */
    public void resAppDeviceUpdate(String resultData) {
        LogHelper.i("resAppDeviceUpdate : " + resultData);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Connect Handle

    /**
     * Response 응답 받는 Callback 함수
     */
    private final IServerCallback mServerCallbackListener = new IServerCallback() {
        @Override
        public void serverResponse(int callbackRspNo, BeanResponseData responseData) {
            if (mIsActivityDestroy) {
                return;
            }

            LogHelper.e("getSuccess : " + responseData.getSuccess());
            if (responseData.getSuccess()) {
                switch (callbackRspNo) {
                    case ServerInfo.IF_RESID_APP_DEVICE_UPDATE:
                        resAppDeviceUpdate(responseData.getMsg());
                        break;
                    default:
                        break;
                }
            } else {
                switch (callbackRspNo) {
                    case ServerInfo.IF_RESID_APP_DEVICE_UPDATE:
                        if (mIsGetDeviceUpdateSuccess) {
                            mIsGetDeviceUpdateSuccess = false;

                            mGetDeviceUpdateHandler.postDelayed(mGetDeviceUpdateRunnable, Constant.GET_DEVICE_UPDATE_HANDLE_DELAY_TIME);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };


    //////////////////////////////////////////////////////////////////////////////////////////
    // Method

    // Push 정보를 받아서 Activity 에서 처리하게 해주는 BroadcastReceiver
    public class PushIntentServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogHelper.e("PushIntentServiceReceiver(Start) ==================> ");
            Bundle bundle = intent.getExtras();

            if (bundle != null && bundle.containsKey(Constant.EXTRA_PUSH_LIST_KEY)) {
                BeanResPushData pushMessage = (BeanResPushData)bundle.get(Constant.EXTRA_PUSH_LIST_KEY);
                showPushPopupActivity(pushMessage);
            }
        }
    }

    // Url Load
    private void loadWebUrl(String url) {
        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put(Constant.WV_HEADER_GUBUN_KEY, Constant.WV_HEADER_GUBUN_VALUE);

        mWvWebView.loadUrl(url, extraHeaders);
    }

    // Url Load
    private void loadWebUrl(String url, String qr, String merNm) {
        try {
            String param = "qr=" + qr + "&merNm=" + URLEncoder.encode(merNm, "UTF-8");

            mWvWebView.postUrl(url, param.getBytes());
        } catch (UnsupportedEncodingException e) {
            LogHelper.e(e);
            mWvWebView.postUrl(url, "".getBytes());
        }
    }

    // Javascript 호출
    private void executeJavascript(String javascript) {
        mWvWebView.loadUrl("javascript:"+ javascript);
        LogHelper.e("callJavascript : " + javascript);
    }

    // Javascript 호출
    private void executeJavascript(String methodName, Object...params){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("javascript:");
        stringBuilder.append(methodName);
        stringBuilder.append("(");
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if(param instanceof String){
                stringBuilder.append("'");
                stringBuilder.append(param);
                stringBuilder.append("'");
            } else if(param instanceof JSONObject){
                stringBuilder.append(param);
            }

            if(i < params.length - 1){
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(")");
        LogHelper.e("callJavascript : " + stringBuilder);
        mWvWebView.loadUrl(stringBuilder.toString());
    }

    // App 종료
    public void finishActivity() {
        new AlertDialog.Builder(mContext)
                .setMessage(R.string.msg_finish_app)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_finish, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        // Droid-X 서비스 종료
                        if(mDroidXLibraryManager != null) {
                            mDroidXLibraryManager.stopService();
                        } else {
                            DroidXLibraryManager.getInstance(getApplicationContext()).stopService();
                        }

                        finish();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                    }
                })
                .create()
                .show();
    }

    // Logout 관련
    private void pageLogout(String message) {
        if (message != null && !message.isEmpty()) {
            new AlertDialog.Builder(mContext)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            loadWebUrl(Constant.MAIN_URL);
                        }
                    })
                    .create()
                    .show();
        }
    }

    // Capture 을 위해 한번더 Permission Check
    private boolean grantExternalStoragePermission() {
        ArrayList<String> checkPermission = new ArrayList<>();

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
            // sdk 28 이하에서만 WRITE_EXTERNAL_STORAGE 권한 필요
            //  POST_NOTIFICATIONS : FCM 푸시 토큰을 위해 필요한 권한.
            checkPermission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            boolean result = UtilHelper.permissionCheck(mContext, checkPermission);
            if (!result){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.REQAC_PERMISSION_WRITE_EXTERNAL_STORAGE);
                return false;
            }
        }
        return true;
    }

    // QrShare Activity 호출
    private void openQrShareActivity(String qrShareUrl) {
        Intent intentQrShare = new Intent(mContext, QrSharePopupActivity.class);
        intentQrShare.putExtra(Constant.EXTRA_QR_SHARE_URL, qrShareUrl);
        if(!isRemoteDialogShowing()){
            startActivityForResult(intentQrShare, Constant.REQAC_QR_SHARE);
            mQrShareUrl = "";
        }
    }

    private void openQrReaderActivity(){
        Intent intentQrReader = new Intent(mContext, QRReaderActivity.class);
        if(!isRemoteDialogShowing()){
            startActivityForResult(intentQrReader, Constant.REQAC_QR_READER);
        }
    }

    private void openQrScanActivity(){
        Intent intentQrScan = new Intent(mContext, QRScanActivity.class);
        if(!isRemoteDialogShowing()){
            startActivityForResult(intentQrScan, Constant.REQAC_QR_SCAN);
        }
    }

    // Network 에러시 흰페이지 띄우고 Alert
    public void networkErrorActivity(String message) {
        mWvWebView.setVisibility(View.GONE);

        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        finish();
                    }
                })
                .create()
                .show();
    }

    // Push Popup 보여주기
//    public void showPushPopup(final BeanResPushData pushMessage) {
//        if (mPushAlertDialog != null ) {
//            mPushAlertDialog.dismiss();
//            mPushAlertDialog = null;
//        }
//
//        Vibrator m_vibrator =  (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        m_vibrator.vibrate(200);
//
//        AlertDialog.Builder pushAlertBuilder = new AlertDialog.Builder(mContext);
//        pushAlertBuilder.setMessage(pushMessage.getMSG());
//        pushAlertBuilder.setPositiveButton(R.string.btn_detail, new DialogInterface.OnClickListener(){
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        String parsingUrl = UtilHelper.parsingPushMoveUrl(pushMessage.getVALUE());
//                        if (!parsingUrl.isEmpty()) {
//                            loadWebUrl(Constant.WEB_VIEW_BASE_URL + parsingUrl);
//                        }
//
//                        mPushAlertDialog = null;
//                    }
//                });
//        pushAlertBuilder.setNegativeButton(R.string.btn_close, new DialogInterface.OnClickListener(){
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        mPushAlertDialog = null;
//                    }
//                });
//        mPushAlertDialog = pushAlertBuilder.create();
//        mPushAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                TextView tv = (TextView)mPushAlertDialog.findViewById(android.R.id.message);
//                TypedArray a = getTheme().obtainStyledAttributes(new int[]{R.attr.dialogMessageStyle});
//                tv.setTextAppearance(mContext,R.style.popup_content_text_style);
//                tv.setGravity(Gravity.CENTER);
//            }
//
//        });
//        mPushAlertDialog.show();
//    }

    // 푸시 영수증 화면 보여주기
    public void showPushPopupActivity(final BeanResPushData pushMessage) {
        if (mPushAlertDialogActivity != null ) {
            mPushAlertDialogActivity.finish();
            mPushAlertDialogActivity = null;
        }

        Vibrator m_vibrator =  (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        m_vibrator.vibrate(200);

        Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.EXTRA_PUSH_LIST_KEY,pushMessage);
        if(!isRemoteDialogShowing()){
            startActivityForResult(intent, Constant.REQAC_RECIPT);
        }
    }

    // Show nFilter Keyboard
    private void showNFilterKeypad(String mode, String name, String len, String desc) {
        nfilter.setMaxLength(Integer.parseInt(len));        //MaxLength
        nfilter.setDesc(desc);

        if (Constant.NFILTER_MODE_CHAR.equals(mode)) {
            // nFilter 문자입력 패드 셋팅
            if( nfilter.isNFilterViewVisibility() == View.VISIBLE) {
                nfilter.nFilterClose(View.GONE);
            }
            nfilter.setFieldName(name);
            nfilter.setOnClickListener( new NFilterOnClickListener() {
                @Override
                public void onNFilterClick(NFilterTO nFilterTO) {
                    nFilterResult( nFilterTO );
                }
            });
            nfilter.onViewNFilter(NFilter.KEYPADCHAR);  // nFilter 화면에 띄워줌
        } else {
            // nFilter 숫자입력 패드 셋팅
            if( nfilter.isNFilterViewVisibility() == View.VISIBLE) {
                nfilter.nFilterClose(View.GONE);
            }
            nfilter.setFieldName(name);
            nfilter.setOnClickListener( new NFilterOnClickListener() {
                @Override
                public void onNFilterClick(NFilterTO nFilterTO) {
                    nFilterResult( nFilterTO );
                }
            });
            nfilter.setBottomReplaceButtonVisible(false);
            nfilter.setNumKeyPadBackGroundColor("#C1C3C8");
            nfilter.onViewNFilter(NFilter.KEYPADSERIALNUM);  // nFilter 화면에 띄워줌
        }
    }

    // Hide nFilter Keyboard
    private boolean hideNFilterKeypad() {
        if( nfilter.isNFilterViewVisibility() == View.VISIBLE){
            nfilter.nFilterClose(View.GONE);
            return true;
        }

        return false;
    }

    // nFilter 리턴값 처리 메서드
    public void nFilterResult(NFilterTO nFilterTO) {

        if( nFilterTO.getFocus() == NFilter.NEXTFOCUS ){
            LogHelper.e("NEXTFOCUS : " + new String(nFilterTO.getFieldName()));

            nfilter.nFilterClose(View.GONE); //nFilter 닫기
        }else if( nFilterTO.getFocus() == NFilter.PREFOCUS ){
            LogHelper.e("PREFOCUS : " + new String(nFilterTO.getFieldName()));

            nfilter.nFilterClose(View.GONE);
        }else if( nFilterTO.getFocus() == NFilter.DONEFOCUS ){  //nFilter 완료
            executeJavascript("closeNFilterKeypadCallBack()");
            nfilter.nFilterClose(View.GONE);
        }else{
            if(nFilterTO.getPlainLength() > 0) {
                LogHelper.e("getFieldName : " + new String(nFilterTO.getFieldName()));

                LogHelper.d("getEncData : " + nFilterTO.getEncData());
                LogHelper.d("getPlainLength : " + nFilterTO.getPlainLength());
                LogHelper.d("getDummyData : " + nFilterTO.getDummyData());
                LogHelper.d("getPlainNormalData : " + nFilterTO.getPlainNormalData());
                LogHelper.d("getPlainData : " + nFilterTO.getPlainData());

                executeJavascript("showNFilterKeypadCallBack",nFilterTO.getEncData(), new String(nFilterTO.getFieldName()), nFilterTO.getDummyData());

                // 입력필드가 가상키보드에 가려서 보이지 않을 경우
                // 임시로 값을 보여주는 editText
                // nfilter_char_key_view.xml 32라인에서 직접 수정 가능
                // nfilter_num_key_view.xml 32라인에서 직접 수정 가능
            } else {
                LogHelper.e("getFieldName : " + new String(nFilterTO.getFieldName()));
                LogHelper.d("==========> No Data");

                executeJavascript("showNFilterKeypadCallBack", "", new String(nFilterTO.getFieldName()), "");
            }
        }
    }

    // Cookie
    public String getCookie(String url,String cookieName){
        String CookieValue = null;

        String cookies = CookieManager.getInstance().getCookie(url);
        if (cookies != null) {
            String[] temp=cookies.split(";");
            for (String ar1 : temp ){
                if(ar1.contains(cookieName)){
                    String[] temp1=ar1.split("=");
                    CookieValue = temp1[1];
                }
            }
        }
        return CookieValue;
    }

    // Push Token 전송 (PushToken 정보가 있을때까지 돌면서 30초에 한번씩 10번까지 시도)
    public void initGetDeviceUpdateHandler() {
        LogHelper.e("initGetDeviceUpdateHandler");
        mIsGetDeviceUpdateSuccess = true;
        mGetDeviceUpdateTryCount = 0;

        if (mGetDeviceUpdateHandler != null) {
            mGetDeviceUpdateHandler.removeCallbacks(mGetDeviceUpdateRunnable);
        } else {
            mGetDeviceUpdateHandler = new Handler();
        }

        mGetDeviceUpdateHandler.postDelayed(mGetDeviceUpdateRunnable, 1000);
    }

    private final Runnable mGetDeviceUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            LogHelper.e("mGetDeviceUpdateRunnable Call : " + mGetDeviceUpdateTryCount);
            mGetDeviceUpdateTryCount++;

            String Uuid = userInfo.getUuid();
            String pushToken = userInfo.getPushToken();

            if ((Uuid != null && !Uuid.isEmpty()) && (pushToken != null && !pushToken.isEmpty())) {
                reqAppDeviceUpdate();
            } else {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        LogHelper.w("Fetching FCM registration token failed", task.getException());
                        mGetDeviceUpdateHandler.postDelayed(mGetDeviceUpdateRunnable, Constant.GET_DEVICE_UPDATE_HANDLE_DELAY_TIME);
                    }else{
                        userInfo.setPushToken(task.getResult());
                    }
                });
                // onCompletelistener에서 실패할 경우 수행하도록 이동함.
//                if (mGetDeviceUpdateTryCount < 10) {
//                    mGetDeviceUpdateHandler.postDelayed(mGetDeviceUpdateRunnable, Constant.GET_DEVICE_UPDATE_HANDLE_DELAY_TIME);
//                }
            }
        }
    };

//    private void setDroidNotification(){
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.dx_notification_icon_w)
//                        .setContentTitle(getString(R.string.dx_notification_title))
//                        .setContentText(getString(R.string.dx_notification_msg));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.dx_notification_title);
//            int importance = NotificationManager.IMPORTANCE_LOW;
//            NotificationChannel channel = new NotificationChannel(Constant.DROIDX_CHANNEL_ID, name, importance);
//            channel.setShowBadge(false);
//            channel.enableVibration(false);
//            notificationManager.createNotificationChannel(channel);
//            builder.setChannelId(Constant.DROIDX_CHANNEL_ID);
//        }else{
//            builder.setPriority(Notification.PRIORITY_LOW);
//        }
//        builder.setOngoing(true);
//        builder.setNumber(0);
//        notificationManager.notify(Constant.DROIDX_NOTI_ID, builder.build());
//    }

    public void showLoadingDialog(int type) {

        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            dismissLoadingDialog();
        } else {
            if (type==Constant.LOADING_TYPE_SECRET){
                findViewById(R.id.loading_background).setVisibility(View.VISIBLE);
                mLoadingDialog = new AppCompatDialog(MainActivity.this, R.style.no_dim_dialog);
            }else{
                mLoadingDialog = new AppCompatDialog(MainActivity.this, R.style.dialog_activity_style);
            }
            mLoadingDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mLoadingDialog.setContentView(R.layout.dialog_loading);
            mLoadingDialog.show();

        }


        final ImageView ivLoading = mLoadingDialog.findViewById(R.id.iv_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) ivLoading.getBackground();
        ivLoading.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tvMsg = mLoadingDialog.findViewById(R.id.tv_msg);
        if (type==Constant.LOADING_TYPE_GENERAL){
            tvMsg.setVisibility(View.GONE);

        }else if (type==Constant.LOADING_TYPE_SECRET){
        }else{
            tvMsg.setVisibility(View.GONE);
        }


    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            findViewById(R.id.loading_background).setVisibility(View.GONE);
            mLoadingDialog.dismiss();
        }
    }

}
