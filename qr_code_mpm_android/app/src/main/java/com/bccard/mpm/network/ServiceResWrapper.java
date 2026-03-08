/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : ServiceResWrapper
 * 작성자명   : 20170448
 * 상세설명   : Server 통신 후 Response 받는 데이터를 파싱하는 클래스
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

package com.bccard.mpm.network;

import android.os.Handler;
import android.os.Looper;

import com.bccard.mpm.network.bean.BeanResponseData;
import com.bccard.mpm.network.bean.BeanResponseResultData;
import com.bccard.mpm.util.LogHelper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

class ServiceResWrapper implements Runnable {
	private String mResponseData = null;

	private final int mCallbackRspNo;
	private final IServerCallback mServerCallback;

	private int mErrorState;
	private String mErrorMsg = null;

	public ServiceResWrapper(final IServerCallback callback, int callbackResNo) {
		this.mResponseData = null;
	    this.mServerCallback = callback;
		this.mCallbackRspNo = callbackResNo;
		this.mErrorState = 0;
		this.mErrorMsg = null;
	}
	 
	public void run() {
	    if( (this.mErrorState != 0) || (this.mErrorMsg != null) || (this.mResponseData == null) ){
			LogHelper.e( "Error ResponseData : " + mResponseData );

			BeanResponseData responseData = new BeanResponseData();
			try {
				if( mErrorMsg != null ) {
					responseData.setCode(ServerInfo.RES_ERROR_CODE);
					responseData.setErrorMsg(mErrorMsg);
				} else if( mErrorState != 0 ) {
					responseData.setCode(ServerInfo.RES_ERROR_CODE);
					responseData.setErrorMsg("mErrorState : " + mErrorState);
				} else if( mResponseData == null ) {
					responseData.setCode(ServerInfo.RES_ERROR_CODE);
					responseData.setErrorMsg("Response Data is Null");
				} else if( mResponseData != null ) {
					try {
						responseData = parsingResultJsonData(mResponseData);
					} catch (JSONException jsonE) {
						LogHelper.printException( jsonE );

						responseData.setCode(ServerInfo.RES_ERROR_CODE);
						responseData.setErrorMsg("JSONException Error 01");
					}
				} else {
					responseData.setCode(ServerInfo.RES_ERROR_CODE);
					responseData.setErrorMsg("Unknow Error 01");
				}

				responseData.setSuccess(false);
			} catch (Exception e) {
				LogHelper.printException( e );

				responseData.setSuccess(false);
				responseData.setCode(ServerInfo.RES_ERROR_CODE);
				responseData.setErrorMsg(e.getMessage());
			}

			final BeanResponseData finalResponseData = responseData;
			LogHelper.e("1========>" + finalResponseData.getSuccess());
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					mServerCallback.serverResponse(mCallbackRspNo, finalResponseData);
				}
			});
	    } else {
			LogHelper.e( "ResponseData : \n" + mResponseData );

			BeanResponseData responseData = new BeanResponseData();
			try {
				try {
					if (mResponseData.contains("<!doctype html>")) {
						responseData.setSuccess(false);
						responseData.setCode(ServerInfo.RES_ERROR_CODE);
						responseData.setErrorMsg("Load Html Page");
					} else {
						responseData = parsingResultJsonData(mResponseData);
					}
				} catch (JSONException jsonE) {
					LogHelper.printException( jsonE );

					responseData.setSuccess(false);
					responseData.setCode(ServerInfo.RES_ERROR_CODE);
					responseData.setErrorMsg("JSONException Error 02");
				}
			} catch (Exception e) {
				LogHelper.printException( e );

				responseData.setSuccess(false);
				responseData.setCode(ServerInfo.RES_ERROR_CODE);
				responseData.setErrorMsg(e.getMessage());
			}

			final BeanResponseData finalResponseData = responseData;
			LogHelper.e("2========>" + finalResponseData.getSuccess());
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					mServerCallback.serverResponse(mCallbackRspNo, finalResponseData);
				}
			});
	    }
	}
	
	public void setResponse(final String responseData) {
	    this.mResponseData = responseData;
	}
	    
	public void setError( int errorState){
	    this.mErrorState = errorState;
	}
	 
	public void setError( String errorMsg ){
	    this.mErrorMsg = errorMsg;
	}
	
	public void setError( Exception e ){
	    this.mErrorMsg = e.toString();
	}

	private BeanResponseData parsingResultJsonData(String responseJsonData) throws JSONException {
		BeanResponseData responseData = new BeanResponseData();

		JSONObject jsonResData = new JSONObject(responseJsonData);
		if (jsonResData.has("MPM")) {
			JSONObject jsonMpm = jsonResData.getJSONObject("MPM");
			JSONObject jsonResult = jsonMpm.getJSONObject("result");
			JSONObject jsonMsg = jsonMpm.getJSONObject("msg");

			LogHelper.e("jsonMpm : " + jsonMpm);
			LogHelper.e("jsonResult : " + jsonResult);
			LogHelper.e("jsonMsg : " + jsonMsg);
			BeanResponseResultData resultData = new Gson().fromJson(jsonResult.toString(), BeanResponseResultData.class);
			String msgData = jsonMsg.toString();
			LogHelper.e("resultData getCode : " + resultData.getCode());
			LogHelper.e("resultData getDesc : " + resultData.getDesc());
			responseData.setSuccess("MP0000".equals(resultData.getCode()));
			responseData.setCode(resultData.getCode());
			responseData.setErrorMsg(resultData.getDesc());
			responseData.setResult(resultData);
			responseData.setMsg(msgData);
		} else {
			BeanResponseResultData resultCode = new BeanResponseResultData();
			resultCode.setCode(ServerInfo.RES_ERROR_CODE);
			resultCode.setDesc("Not Has MPM");

			responseData.setCode(ServerInfo.RES_ERROR_CODE);
			responseData.setErrorMsg("Not Has MPM");
			responseData.setResult(resultCode);
		}

		return responseData;
	}
}