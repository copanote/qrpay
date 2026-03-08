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

package com.bccard.mpm.ui.common;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.bccard.mpm.R;
import com.bccard.mpm.common.BaseActivity;
import com.bccard.mpm.common.Constant;
import com.bccard.mpm.util.LogHelper;
import com.bccard.mpm.util.UtilHelper;

import java.util.ArrayList;

public class PermissionActivity extends BaseActivity {

//    public Button btn_permission_finish = null;
    public Button btn_permission_ok = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }
        setContentView(R.layout.act_permission);

        // 강제로 디스플레이의 90퍼센트로 팝업의 가로사이즈 변경
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.9);
        getWindow().getAttributes().width = width;

        this.setFinishOnTouchOutside(false);

        initControl();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogHelper.e("PermissionActivity onRequestPermissionsResult");
        LogHelper.e("requestCode : "+requestCode);
        if (requestCode == Constant.REQAC_PERMISSION_PERMISSION) {
            boolean allGrantCheck = false;
            for (int grantRes : grantResults) {
                LogHelper.e("grantRes : " + grantRes);
                if (grantRes == PackageManager.PERMISSION_GRANTED) {
                    allGrantCheck = true;
                } else {
                    allGrantCheck = false;
                    break;
                }
            }

            if (allGrantCheck) {
                // Manage External Storage 권한 체크 불필요
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (!Environment.isExternalStorageManager()) {
//                        showManageStorageDialog();
//                    } else {
//                        setResult(RESULT_OK);
//                        finish();
//                    }
//                } else {
                    setResult(RESULT_OK);
                    finish();
//                }
            } else {
                showPermissionDialog();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Init

    public void initControl() {
//        btn_permission_finish = (Button) findViewById(R.id.btn_permission_finish);
        btn_permission_ok = findViewById(R.id.btn_permission_ok);

//        btn_permission_finish.setOnClickListener(onClickListener);
        btn_permission_ok.setOnClickListener(onClickListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Event Listener

    /**
     * Click Listener
     */
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
               /* RESULT_CANCELED  case R.id.btn_permission_finish:
                    setResult();
                    finish();
                    break;*/
                case R.id.btn_permission_ok:
                    ArrayList<String> checkPermission = new ArrayList<>();

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        // sdk 33 이상
                        //  POST_NOTIFICATIONS : FCM 푸시 토큰을 위해 필요한 권한.
                        checkPermission.add(Manifest.permission.POST_NOTIFICATIONS);
                    }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // sdk 29 이상
                        //  MANAGE_EXTERNAL_STORAGE권한을 삭제하고 Media api로 변경으로 인해 필요한 권한 없음
                    }else {
                        // sdk 28 이하
                        //  WRITE_EXTERNAL_STORAGE 권한 필요
                        checkPermission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                    UtilHelper.permissionCheckAndRequest(mContext, checkPermission, Constant.REQAC_PERMISSION_PERMISSION);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogHelper.e("PermissionActivity onActivityResult");
        LogHelper.e("requestCode : "+requestCode+", resultCode : "+resultCode);
        if (requestCode == Constant.REQAC_PERMISSION_APP_SETTING) {
            // 앱설정으로 갔다 왔기 때문에 사용자가 기존에 동의했던 권한도 비동의 처리할수 있으므로
            // 모든 권한을 다시 체크해야 함.
            ArrayList<String> checkPermission = new ArrayList<String>();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                checkPermission.add(Manifest.permission.POST_NOTIFICATIONS);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // sdk 29 이상
                //  MANAGE_EXTERNAL_STORAGE권한을 삭제하고 Media api로 변경으로 인해 필요한 권한 없음
            }else {
                checkPermission.add(0, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!UtilHelper.permissionCheck(mContext, checkPermission)) {
                showPermissionDialog();
            }else{
                setResult(RESULT_OK);
                finish();
            }
        }
//        else if (requestCode == REQAC_PERMISSION_MANAGE_STORAGE) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (!Environment.isExternalStorageManager()) {
//                    showManageStorageDialog();
//                } else {
//                    setResult(RESULT_OK);
//                    finish();
//                }
//            }
//        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.R)
//    void requestManageExternalStoragePermission(){
//        try { // 내 앱의 권한 요청창으로 이동
//            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).
//                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    .setData(Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, REQAC_PERMISSION_MANAGE_STORAGE);
//        } catch (Exception e) {
//            // 실패시 모든 앱의 권한 요청 관리 설정창으로 이동
//            e.printStackTrace();
//            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).
//                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivityForResult(intent, REQAC_PERMISSION_MANAGE_STORAGE);
//        }
//    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // AlertDialog
    androidx.appcompat.app.AlertDialog moveAppDetailDialog = null;

    androidx.appcompat.app.AlertDialog moveManageStorageDialog = null;
    private void showPermissionDialog() {
        if (moveAppDetailDialog == null) {
            moveAppDetailDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext)
                    .setMessage(R.string.msg_permission_denied)
                    .setCancelable(false)
                    .setNegativeButton(R.string.btn_finish, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    })
                    .setPositiveButton(R.string.btn_setting, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            moveAppDetailDialog.dismiss();
                            UtilHelper.moveSystemSettingAppDetailsForResult(PermissionActivity.this);
                        }
                    })
                    .create();
        }
        moveAppDetailDialog.show();
    }

//    private void showManageStorageDialog(){
//        if (moveManageStorageDialog == null) {
//            moveManageStorageDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext)
//                    .setMessage(R.string.msg_droidx_permission_denied)
//                    .setCancelable(false)
//                    .setNegativeButton(R.string.btn_finish, new DialogInterface.OnClickListener(){
//                        public void onClick(DialogInterface dialog, int whichButton){
//                            setResult(RESULT_CANCELED);
//                            finish();
//                        }
//                    })
//                    .setPositiveButton(R.string.btn_setting, new DialogInterface.OnClickListener(){
//                        public void onClick(DialogInterface dialog, int whichButton){
//                            moveManageStorageDialog.dismiss();
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                                requestManageExternalStoragePermission();
//                            }
//                        }
//                    })
//                    .create();
//        }
//        moveManageStorageDialog.show();
//    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // AlertDialog End
}
