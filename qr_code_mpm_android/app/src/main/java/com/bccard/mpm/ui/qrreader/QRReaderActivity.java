package com.bccard.mpm.ui.qrreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class QRReaderActivity extends BaseActivity implements View.OnClickListener {


    private static final int TAB_SCAN = 1;
    private static final int TAB_SERIAL_NUMBER = 2;

    private CaptureManager capture;

    private int currentTab;
    private Button btnScan,btnSerialNumber;
    private LinearLayout layoutbarcodeScannerView, layoutSerialNumber;
    private DecoratedBarcodeView barcodeScannerView;
    private EditText etSerialNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_qr_reader);
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
        currentTab = TAB_SERIAL_NUMBER;

        ((TextView)findViewById(R.id.tv_title)).setTypeface(null, Typeface.BOLD);
        findViewById(R.id.btn_close).setOnClickListener(this);

        btnScan = findViewById(R.id.btn_scan);
        btnSerialNumber = findViewById(R.id.btn_serial_number);
        layoutbarcodeScannerView = findViewById(R.id.layout_zxing_barcode_scanner);
        layoutSerialNumber = findViewById(R.id.layout_serial_number);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        etSerialNumber = findViewById(R.id.et_serial_number);
        btnScan.setOnClickListener(this);
        btnSerialNumber.setOnClickListener(this);

        findViewById(R.id.btn_confirm).setOnClickListener(this);
        initCamera(savedInstanceState);

        btnSerialNumber.performClick();

        ArrayList<String> checkPermission = new ArrayList<String>();
        checkPermission.add(Manifest.permission.CAMERA);
        boolean permissionAgreed = UtilHelper.permissionCheckAndRequest(mContext, checkPermission, Constant.REQAC_PERMISSION_PERMISSION);
        if (permissionAgreed){
            btnScan.performClick();
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
                btnScan.performClick();
            } else {
                btnSerialNumber.performClick();
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
        if (currentTab == TAB_SCAN){
            capture.onResume();
        }
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

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSerialNumber.getWindowToken(), 0);
        changeTab(v.getId());
    }

    private void changeTab(int id){
        switch (id){
            case R.id.btn_close:
            case R.id.btn_confirm:
                finishActivity();
                break;
            case R.id.btn_scan:
                ArrayList<String> checkPermission = new ArrayList<String>();
                checkPermission.add(Manifest.permission.CAMERA);
                boolean permissionAgreed = UtilHelper.permissionCheckAndRequest(mContext, checkPermission, Constant.REQAC_PERMISSION_PERMISSION);
                if (permissionAgreed){
                    currentTab = TAB_SCAN;

                    layoutbarcodeScannerView.setVisibility(View.VISIBLE);
                    btnScan.setTextColor(getResources().getColor(R.color.colorTextPressed));
                    btnScan.setBackgroundResource(R.drawable.btn_qrreader_tab_press);
                    layoutSerialNumber.setVisibility(View.INVISIBLE);
                    btnSerialNumber.setTextColor(getResources().getColor(R.color.colorTextGray));
                    btnSerialNumber.setBackgroundResource(R.drawable.btn_qrreader_tab_normal);

                    capture.onResume();
                }else{
                    btnSerialNumber.performClick();
                }
                break;
            case R.id.btn_serial_number:
                currentTab = TAB_SERIAL_NUMBER;
                layoutbarcodeScannerView.setVisibility(View.INVISIBLE);
                btnScan.setTextColor(getResources().getColor(R.color.colorTextGray));
                btnScan.setBackgroundResource(R.drawable.btn_qrreader_tab_normal);
                layoutSerialNumber.setVisibility(View.VISIBLE);
                btnSerialNumber.setTextColor(getResources().getColor(R.color.colorTextPressed));
                btnSerialNumber.setBackgroundResource(R.drawable.btn_qrreader_tab_press);
                capture.onPause();
                break;

        }
    }

    private void finishActivity(){
        Intent resultIntent = new Intent();
        String decalCode = "";
        if (currentTab==TAB_SCAN){

        }else{
            decalCode = etSerialNumber.getText().toString().trim();
            LogHelper.e("DECAL_CODE : " + decalCode);

            resultIntent.putExtra(Constant.EXTRA_DECAL_CODE, decalCode);
            setResult( RESULT_OK, resultIntent );
        }
        finish();
    }
}