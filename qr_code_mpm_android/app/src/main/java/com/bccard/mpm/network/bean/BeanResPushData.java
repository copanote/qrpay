/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : BeanResPushData
 * 작성자명   : 20170448
 * 상세설명   : Polling 정보를 가지는 Bean(VO)
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

package com.bccard.mpm.network.bean;

import java.io.Serializable;

public class BeanResPushData implements Serializable {
	private String SEQ = "";
	private String TITLE = "";
	private String MSG = "";
	private String VALUE = "";

	public String getSEQ() {
		return SEQ;
	}

	public void setSEQ(String SEQ) {
		this.SEQ = SEQ;
	}

	public String getTITLE() {
		return TITLE;
	}

	public void setTITLE(String TITLE) {
		this.TITLE = TITLE;
	}

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String MSG) {
		this.MSG = MSG;
	}

	public String getVALUE() {
		return VALUE;
	}

	public void setVALUE(String VALUE) {
		this.VALUE = VALUE;
	}

	public String toString() {
		return String.format("BeanResPushData:SEQ=%s, TITLE=%s, MSG=%s, VALUE=%s",
				new Object[]{this.SEQ, this.TITLE, this.MSG, this.VALUE});
	}
}
