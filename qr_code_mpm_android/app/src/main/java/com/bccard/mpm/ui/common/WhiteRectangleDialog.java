package com.bccard.mpm.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bccard.mpm.R;

/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : WhiteRectangleDialog
 * 작성자명   : 20149060
 * 상세설명   : 
 * 적용범위   : mpm
 * 작성일자   : 09/05/2019
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
public class WhiteRectangleDialog extends Dialog {

    String mContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.white_solid_rectangle_content_one_button_dialog);

        TextView contentTv = findViewById(R.id.tv_content);
        contentTv.setText(mContent);
        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public WhiteRectangleDialog(@NonNull Context context, String content) {
        super(context);
        mContent = content;
    }
}
