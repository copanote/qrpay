/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : QrSharePopupActivity
 * 작성자명   : 20170448
 * 상세설명   : 공유 웹뷰를 가지는 QrSharePopupActivity
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

package com.bccard.mpm.ui.qrshare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bccard.mpm.R;
import com.bccard.mpm.common.BaseActivity;
import com.bccard.mpm.common.Constant;
import com.bccard.mpm.util.FileHelper;
import com.bccard.mpm.util.LogHelper;
import com.bccard.mpm.util.SharedPrefHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class QrSharePopupActivity extends BaseActivity {
    private Context mContext = null;
    private WebView mWvWebView = null;

    private Button mBtnClose = null;
    private Button mBtnShare = null;

    private QrShareWebClient mWebClient = null;
    private QrShareWebInterface mWebInterface = null;
    private QrShareChromeClient mChromeClient = null;

    private String mQrUrl = "";
    private final String mKeyword = "android";

    private View mRootView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }

        setContentView(R.layout.act_qr_share_popup);
        mRootView = findViewById(R.id.root);
        mContext = QrSharePopupActivity.this;

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.createInstance(this);
        }

        if (getIntent().hasExtra(Constant.EXTRA_QR_SHARE_URL)) {
            mQrUrl = getIntent().getStringExtra(Constant.EXTRA_QR_SHARE_URL);
        }

        // 강제로 디스플레이의 90퍼센트로 팝업의 가로사이즈 변경
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth() - 2 * getResources().getDimensionPixelSize(R.dimen.popup_share_outer_padding);
        getWindow().getAttributes().width = width;

        boolean isNeedDecreaseMargin = isNeedDecreaseMargin();
        if (isNeedDecreaseMargin) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.ll_info).getLayoutParams();
            params.setMargins(0, getResources().getDimensionPixelSize(R.dimen.popup_share_header_margin), 0, 0);

            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) findViewById(R.id.ll_btn).getLayoutParams();
            params2.setMargins(0, getResources().getDimensionPixelSize(R.dimen.popup_share_header_margin), 0, 0);
        }

        initControl();
    }

    private boolean isNeedDecreaseMargin() {
        switch (this.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
            case DisplayMetrics.DENSITY_MEDIUM:
            case DisplayMetrics.DENSITY_TV:
            case DisplayMetrics.DENSITY_HIGH:
            case DisplayMetrics.DENSITY_260:
            case DisplayMetrics.DENSITY_280:
            case DisplayMetrics.DENSITY_300:
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_340:
            case DisplayMetrics.DENSITY_360:
            case DisplayMetrics.DENSITY_400:
            case DisplayMetrics.DENSITY_420:
            case DisplayMetrics.DENSITY_XXHIGH:
                Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height = size.y;
                if (height < 1920) {
                    return true;
                }
            case DisplayMetrics.DENSITY_560:
            case DisplayMetrics.DENSITY_XXXHIGH:
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.getInstance().startSync();
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
        FileHelper.deleteAllFile(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if (requestCode == Constant.REQAC_PERMISSION_WRITE_EXTERNAL_STORAGE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    screenCapture();
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Init

    public void initControl() {
        mBtnClose = findViewById(R.id.btn_qr_share_close);
        mBtnShare = findViewById(R.id.btn_qr_share_share);

        mBtnClose.setOnClickListener(onClickListener);
        mBtnShare.setOnClickListener(onClickListener);

        // 웹뷰 셋팅
        mWvWebView = findViewById(R.id.wv_qr_share_view);
        setWebview();
    }

    private void setWebview() {
        mWebClient = new QrShareWebClient(mContext);
        mWebClient.setCallbackQrShare(mWebEventCallbackHandler);
        mWebInterface = new QrShareWebInterface(mContext);
        mWebInterface.setCallbackQrShare(mWebEventCallbackHandler);
        mChromeClient = new QrShareChromeClient(mContext);

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
//        mWvWebView.getSettings().setDomStorageEnabled(false);

        mWvWebView.addJavascriptInterface(mWebInterface, mKeyword);
        mWvWebView.setWebChromeClient(mChromeClient);

//        CookieManager.getInstance().removeSessionCookie();
//        CookieManager.getInstance().removeAllCookie();

        LogHelper.w("mQrUrl : " + mQrUrl);
        if (mQrUrl != null && !mQrUrl.isEmpty()) {
            String qr = SharedPrefHelper.getSharedMpmData(mContext, Constant.PREF_MPM_KEY_QR);
            String merNm = SharedPrefHelper.getSharedMpmData(mContext, Constant.PREF_MPM_KEY_MERNM);

            loadWebUrl(Constant.WEB_VIEW_BASE_URL + mQrUrl, qr, merNm);
        } else {
            showToast(R.string.msg_qr_share_url_fail);
            finish();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Event Listener

    /**
     * Click Listener
     */
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_qr_share_close:
                    finish();
                    break;
                case R.id.btn_qr_share_share:
                    boolean isGrantStorage = grantExternalStoragePermission();

                    if (isGrantStorage) {
                        screenCapture();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////
    // Handler

    /**
     * WebEvent 응답 받는 Callback 함수
     */
    @SuppressLint("HandlerLeak")
    private final Handler mWebEventCallbackHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Constant.WM_WV_ONLOAD:
                    LogHelper.e("WM_WV_ONLOAD");
//                    mWvWebView.loadUrl("javascript:$(\".body\").css(\"margin\", \"-185px 0 0 -145px\");");
//                    mWvWebView.loadUrl("javascript:$(\".box-wrap\").css(\"margin\", \"0 0 0 0\");");
//                    mWvWebView.loadUrl("javascript:$(\".box-wrap\").css(\"width\", \"288px\");");
//                    mWvWebView.loadUrl("javascript:$(\".box-wrap\").css(\"height\", \"370px\");");
                    dismissLoadingDialog();
                    QrSharePopupActivity.this.mRootView.setVisibility(View.VISIBLE);
                    break;
                case Constant.WM_WV_LOGOUT:
                    String message = (String) msg.obj;

                    Intent intentResult = new Intent();
                    intentResult.putExtra(Constant.EXTRA_QR_LOGOUT, message);
                    setResult(RESULT_OK, intentResult);
                    finish();
                    break;
                case Constant.WM_WV_ERROR_HOST_LOOKUP:
                case Constant.WM_WV_ERROR:
                    String messageError = (String) msg.obj;

                    Intent intentResultError = new Intent();
                    intentResultError.putExtra(Constant.EXTRA_QR_ERROR, messageError);
                    setResult(RESULT_OK, intentResultError);
                    finish();

                    LogHelper.e("WM_WV_ERROR : " + messageError);
                    break;
                case Constant.WM_WV_DISMISS_LOADING:
                    dismissLoadingDialog();
                    QrSharePopupActivity.this.mRootView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////
    // Method

    private void loadWebUrl(String url) {
        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put(Constant.WV_HEADER_GUBUN_KEY, Constant.WV_HEADER_GUBUN_VALUE);

        mWvWebView.loadUrl(url, extraHeaders);
    }

    // Url Load
    private void loadWebUrl(String url, String qr, String merNm) {
        showLoadingDialog(Constant.LOADING_TYPE_GENERAL);
        try {
            String param = "qr=" + qr + "&merNm=" + URLEncoder.encode(merNm, "UTF-8");

            LogHelper.e("SubMain url : " + url);
            LogHelper.e("SubMain Param : " + param);
            mWvWebView.postUrl(url, param.getBytes());
        } catch (UnsupportedEncodingException e) {
            LogHelper.e(e);
            mWvWebView.postUrl(url, "".getBytes());
        }
    }

    private boolean grantExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // sdk 29 이상
            //  MANAGE_EXTERNAL_STORAGE권한을 삭제하고 Media api로 변경으로 인해 필요한 권한 없음
            return true;
        }else {
            // sdk 28 이하
            //  WRITE_EXTERNAL_STORAGE 권한 필요
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.REQAC_PERMISSION_WRITE_EXTERNAL_STORAGE);
                    return false;
                }
            } else {
                return true;
            }
        }

    }

    // Capture
    private void screenCapture() {

        FileOutputStream outputStreamWeb = null;
        try {
            int quality = 100;

            mWvWebView.setDrawingCacheEnabled(true);
            Bitmap bitmapWeb = Bitmap.createBitmap(mWvWebView.getDrawingCache());
            mWvWebView.setDrawingCacheEnabled(false);

            Uri uri;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 안드로이드10 이상은 media api 사용
                String fileName = "qr" + ".jpg";
                ContentValues contentValues = new ContentValues();

                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/QRPayforShop"); // 경로 설정
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName); // 파일이름을 put해준다.
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                // 현재 is_pending 상태임을 만들어준다.
                // 다른 곳에서 이 데이터를 요구하면 무시하라는 의미로, 해당 저장소를 독점할 수 있다.
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 1);

                ContentResolver contentResolver = getContentResolver();
                uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                // write mode로 파일 open
                ParcelFileDescriptor image = contentResolver.openFileDescriptor(uri, "w", null);
                if (image != null){
                    outputStreamWeb = new  FileOutputStream(image.getFileDescriptor());
                    bitmapWeb.compress(Bitmap.CompressFormat.JPEG, quality, outputStreamWeb);
                    outputStreamWeb.flush();
                    outputStreamWeb.close();

                    contentValues.clear();
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0); // 저장소 독점을 해제한다.
                    contentResolver.update(uri, contentValues, null, null);
                }
            } else {
                String mWebviewPath = FileHelper.getDirectory(getApplicationContext()).getAbsolutePath() + "/" + "qr" + ".jpg";
                LogHelper.w(mWebviewPath);

                // 이미지 파일 생성
                File imageFileWeb = new File(mWebviewPath);
                outputStreamWeb = new FileOutputStream(imageFileWeb);
                bitmapWeb.compress(Bitmap.CompressFormat.JPEG, quality, outputStreamWeb);
                outputStreamWeb.flush();
                outputStreamWeb.close();

                // android 8.0 issue fix
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    uri = FileProvider.getUriForFile(mContext, "com.bccard.mpm.qrshareprovider", imageFileWeb);
                } else {
                    uri = Uri.fromFile(imageFileWeb);
                }
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");

            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            if(!isRemoteDialogShowing()){
                startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
            }
        } catch (Throwable e) {
            LogHelper.printException(e);
            Toast.makeText(mContext, R.string.msg_file_make_fail, Toast.LENGTH_SHORT).show();
        } finally {
            if (outputStreamWeb != null) {
                try {
                    outputStreamWeb.close();
                } catch (IOException e) {
                    LogHelper.e(e);
                }
            }
        }
    }
}
