package com.bccard.mpm.ui.common;

import android.content.Context;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bccard.mpm.R;
import com.bccard.mpm.common.BaseActivity;
import com.bccard.mpm.common.Constant;
import com.bccard.mpm.network.bean.BeanResPushData;
import com.bccard.mpm.util.SharedPrefHelper;
import com.bccard.mpm.util.UtilHelper;

import java.text.NumberFormat;
import java.util.HashMap;

public class ReceiptActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }
        setContentView(R.layout.act_receipt);

        // 강제로 디스플레이의 90퍼센트로 팝업의 가로사이즈 변경
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.9);
        getWindow().getAttributes().width = width;

        initControl();
    }

    public void initControl() {
        String merNm = SharedPrefHelper.getSharedMpmData(getApplicationContext(),Constant.PREF_MPM_KEY_MERNM);

        findViewById(R.id.btn_close).setOnClickListener(this);
        TextView tvMemNm = findViewById(R.id.tv_mer_nm);
        tvMemNm.setText(merNm);

        TextView tvAuthAton = findViewById(R.id.tv_auth_aton);
        TextView tvTrnsAmt = findViewById(R.id.tv_trns_amt);
        TextView tvTrnsStat = findViewById(R.id.tv_trns_stat);
        TextView tvSvcClssNm = findViewById(R.id.tv_svc_clss_nm);
        TextView tvAuthAton2 = findViewById(R.id.tv_auth_aton2);
        TextView tvCardCoAuthNo = findViewById(R.id.tv_card_co_auth_no);
        TextView tvMpanNo = findViewById(R.id.tv_mpan_no);
        TextView tvInsTrm = findViewById(R.id.tv_ins_trm);
        TextView tvTrnsUniqNo = findViewById(R.id.tv_trns_uniq_no);
        TextView tvAffiCoTrnsUniqNo = findViewById(R.id.tv_affi_co_trns_uniq_no);
        TextView tvDcBefAmt = findViewById(R.id.tv_dc_bef_amt);
        TextView tvDcAmt = findViewById(R.id.tv_dc_amt);
        TextView tvDcAftrAmt = findViewById(R.id.tv_dc_aftr_amt);
//        TextView tvTrnsStat2 = findViewById(R.id.tv_trns_stat2);

        LinearLayout llAffiCoTrnsUniqNo = findViewById(R.id.ll_receipt_affi_co_trns_uniq_no);
        LinearLayout llDcBefAmt = findViewById(R.id.ll_receipt_dc_bef_amt);
        LinearLayout llDcAmt = findViewById(R.id.ll_receipt_dc_amt);
        LinearLayout llDcAftrAmt = findViewById(R.id.ll_receipt_dc_aftr_amt);

        findViewById(R.id.btn_affi_co_trns_uniq_no_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteRectangleDialog dialog = new WhiteRectangleDialog(ReceiptActivity.this, getString(R.string.receipt_voucher_help_info));
                dialog.show();
            }
        });

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey(Constant.EXTRA_PUSH_LIST_KEY)) {
            BeanResPushData pushMessage = (BeanResPushData)bundle.get(Constant.EXTRA_PUSH_LIST_KEY);
            HashMap<String,String> msgMap = UtilHelper.parsingPushMSG(pushMessage.getVALUE());

            tvAuthAton.setText(UtilHelper.getReformmattedDateString(msgMap.get("AUTH_ATON"), "yyyyMMddHHmmss","yyyy-MM-dd HH:mm:ss"));

            tvTrnsAmt.setText(NumberFormat.getIntegerInstance().format(Integer.parseInt(msgMap.get("TRNS_AMT"))));

            tvTrnsStat.setText(msgMap.get("TRNS_STAT_NM"));
//            tvTrnsStat2.setText(msgMap.get("TRNS_STAT_NM"));
            tvSvcClssNm.setText(msgMap.get("SVC_CLSS_NM"));
            tvAuthAton2.setText(UtilHelper.getReformmattedDateString(msgMap.get("AUTH_ATON"), "yyyyMMddHHmmss","yyyy-MM-dd HH:mm:ss"));
            tvCardCoAuthNo.setText(msgMap.get("CARD_CO_AUTH_NO"));
            tvMpanNo.setText(msgMap.get("MPAN_NO"));
            int insTrm = Integer.parseInt(msgMap.get("INS_TRM"));
            if (insTrm>0){
                tvInsTrm.setText(msgMap.get("INS_TRM")+" 개월");
            }else{
                tvInsTrm.setText("일시불");
            }
            tvTrnsUniqNo.setText(msgMap.get("TRNS_UNIQ_NO"));
            if (msgMap.get("SVC_CLSS_NM").contains("유니온페이")){
                tvAffiCoTrnsUniqNo.setText(msgMap.get("AFFI_CO_TRNS_UNIQ_NO"));
            }else{
                llAffiCoTrnsUniqNo.setVisibility(View.GONE);
            }
            int dcAmt = Integer.parseInt(msgMap.get("DC_AMT"));
            if (dcAmt>0){
                tvDcBefAmt.setText(NumberFormat.getIntegerInstance().format(Integer.parseInt(msgMap.get("DC_BEF_AMT"))) + " 원");
                tvDcAmt.setText(NumberFormat.getIntegerInstance().format(Integer.parseInt(msgMap.get("DC_AMT"))) + " 원");
                tvDcAftrAmt.setText(NumberFormat.getIntegerInstance().format(Integer.parseInt(msgMap.get("DC_AFTR_AMT"))) + " 원");
            }else{
                llDcBefAmt.setVisibility(View.GONE);
                llDcAmt.setVisibility(View.GONE);
                llDcAftrAmt.setVisibility(View.GONE);
           }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_close:
            finish();
            break;
        }
    }
}
