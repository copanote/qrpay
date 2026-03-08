package com.bccard.mpm.ui.qrscan;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.bccard.mpm.R;
import com.bccard.mpm.common.BaseActivity;
import com.bccard.mpm.common.Constant;
import com.bccard.mpm.util.LogHelper;
import com.bccard.mpm.util.UtilHelper;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;

public class QRScanActivity extends BaseActivity implements View.OnClickListener {


    private CaptureManager capture;

    private DecoratedBarcodeView barcodeScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_qr_scan);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        
        ((TextView)findViewById(R.id.tv_title)).setTypeface(null, Typeface.BOLD);
        findViewById(R.id.btn_close).setOnClickListener(this);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        initCamera(savedInstanceState);

        ArrayList<String> checkPermission = new ArrayList<String>();
        checkPermission.add(Manifest.permission.CAMERA);
        boolean permissionAgreed = UtilHelper.permissionCheckAndRequest(mContext, checkPermission, Constant.REQAC_PERMISSION_PERMISSION);
        if (permissionAgreed){
            capture.onResume();
        }else{
//            new AlertDialog.Builder(QRScanActivity.this)
//                    .setMessage(R.string.msg_finish_app)
//                    .setCancelable(false)
//                    .setPositiveButton(R.string.btn_finish, new DialogInterface.OnClickListener(){
//                        public void onClick(DialogInterface dialog, int whichButton){
//                            finish();
//                        }
//                    })
//                    .create()
//                    .show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==Constant.REQAC_PERMISSION_PERMISSION) {
            boolean allGrantCheck = false;
            for(int grantRes:grantResults) {
                LogHelper.e("grantRes : " + grantRes);
                if (grantRes == PackageManager.PERMISSION_GRANTED) {
                    allGrantCheck = true;
                } else {
                    allGrantCheck = false;
                    break;
                }
            }

            if (allGrantCheck) {
                capture.onResume();
            } else {
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setMessage(R.string.dialog_scan_permission_denied)
                        .setCancelable(false)
                        .setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        })
                        .setPositiveButton(R.string.btn_setting, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                UtilHelper.moveSystemSettingAppDetailsForResult(QRScanActivity.this);
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        }
    }


    private void initCamera(Bundle savedInstanceState){
        capture = new CaptureManager(this, barcodeScannerView);
        Intent intent = getIntent();
        intent.putExtra(Intents.Scan.BEEP_ENABLED, false);
        capture.initializeFromIntent(intent, savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private void finishActivity(){
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_close:
                finishActivity();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Constant.REQAC_PERMISSION_APP_SETTING){
            ArrayList<String> checkPermission = new ArrayList<String>();
            checkPermission.add(Manifest.permission.CAMERA);
            boolean permissionAgreed = UtilHelper.permissionCheck(mContext, checkPermission);
            if (permissionAgreed){
                capture.onResume();
            }else{
                finishActivity();
            }
        }
    }
}