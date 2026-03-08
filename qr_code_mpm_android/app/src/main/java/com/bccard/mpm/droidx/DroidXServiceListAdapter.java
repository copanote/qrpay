/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : DroidXServiceListAdapter
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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bccard.mpm.R;

public class  DroidXServiceListAdapter extends ArrayAdapter<String> {
	
	static class ResultHolder
	{
		RelativeLayout rlRowScanresult;
		TextView imgTypeIcon;
		TextView tvCause;
		TextView tvInform;
		TextView tvFilepath;
		CheckBox imgHandle;
	}
	
	private final Context context;
	private final List<String> data;
	private final boolean[] bChecked;

	DroidXServiceListAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);

		this.context = context;
		this.data = objects;
		bChecked = new boolean[this.data.size()];
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ResultHolder holder;

		if(row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(R.layout.sample_activity_malware_row_list, parent, false);

			holder = new ResultHolder();
			holder.rlRowScanresult = row.findViewById(R.id.rl_row_scanresult);
			holder.imgTypeIcon = row.findViewById(R.id.img_scanresult_row_icon);
			holder.tvCause = row.findViewById(R.id.tv_scanresult_row_cause);
			holder.tvInform = row.findViewById(R.id.tv_scanresult_row_inform);
			holder.tvFilepath = row.findViewById(R.id.tv_scanresult_row_filepath);
			holder.imgHandle = row.findViewById(R.id.img_scanresult_row_handle);
						
			row.setTag(holder);
		} else {
			holder = (ResultHolder)row.getTag();
		}

		String strdata = data.get(position);
		if(strdata != null) {
			String[] tmpstrs = strdata.split("\n");
			for(String tmps : tmpstrs) {
				if(tmps.startsWith("Desc: ")) {
					holder.tvInform.setText(tmps.substring(6));
				} else if(tmps.startsWith("Path: ")) {
					holder.tvFilepath.setText(tmps.substring(6));
				} else if(tmps.startsWith("Package: ")) {
					holder.tvCause.setText(tmps.substring(9));
				}
			}
		}
		
		holder.imgHandle.setOnCheckedChangeListener((buttonView, isChecked) -> setChecked(position, isChecked));
		
		if(bChecked != null)
			holder.imgHandle.setChecked(bChecked[position]);

		return row;
	}
	
	void setAllChecked(boolean bChecked) {
		for(int i=0;i<this.bChecked.length;i++) {
			this.bChecked[i] = bChecked;
		}
	}

	private void setChecked(int position, boolean bChecked) {
		this.bChecked[position] = bChecked;
	}

	boolean getChecked(int position) {
		return this.bChecked[position];
	}
}
