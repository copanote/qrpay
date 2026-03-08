/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : ServerConnecter
 * 작성자명   : 20170448
 * 상세설명   : Server 통신 클래스
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

import android.content.Context;
import android.net.Uri;

import com.bccard.mpm.util.LogHelper;
import com.bccard.mpm.util.UtilHelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;


public class ServerConnecter {
    private Context mContext = null;
    private HttpURLConnection mHttpUrlConn = null;
    private boolean mShowProgress = false;

    private IServerCallback mServerCallback = null;

    public ServerConnecter(Context context, IServerCallback serverCallback) {
        this.mContext = context;
        this.mServerCallback = serverCallback;

        this.mHttpUrlConn = null;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean requestHttpPost(final int callbackResNo, final String serviceId, final JSONObject params) {
        final ServiceResWrapper httpResWrapper = new ServiceResWrapper(mServerCallback, callbackResNo);

        new Thread(new Runnable() {
            public void run() {
                connectPost(serviceId, httpResWrapper, params, ServerInfo.DEFAULT_PAGE_NO, ServerInfo.DEFAULT_PAGE_LODING_ITEM_COUNT);
            }
        }).start();

        return true;
    }

///////////////////////////////////

    public boolean requestHttpPost(final int callbackResNo, final String serviceId, final JSONObject params, final int pageNo) {
        final ServiceResWrapper httpResWrapper = new ServiceResWrapper(mServerCallback, callbackResNo);

        new Thread(new Runnable() {
            public void run() {
                connectPost(serviceId, httpResWrapper, params, pageNo, ServerInfo.DEFAULT_PAGE_LODING_ITEM_COUNT);
            }
        }).start();

        return true;
    }

    public boolean requestHttpPost(final int callbackResNo, final String serviceId, final JSONObject params, final int pageNo, boolean showProgress) {
        this.mShowProgress = showProgress;
        final ServiceResWrapper httpResWrapper = new ServiceResWrapper(mServerCallback, callbackResNo);

        new Thread(new Runnable() {
            public void run() {
                connectPost(serviceId, httpResWrapper, params, pageNo, ServerInfo.DEFAULT_PAGE_LODING_ITEM_COUNT);
            }
        }).start();

        return true;
    }


///////////////////////////////////

    public boolean requestHttpPost(final int callbackResNo, final String serviceId, final JSONObject params, final int pageNo, final int pageItemCount) {
        final ServiceResWrapper httpResWrapper = new ServiceResWrapper(mServerCallback, callbackResNo);

        new Thread(new Runnable() {
            public void run() {
                connectPost(serviceId, httpResWrapper, params, pageNo, pageItemCount);
            }
        }).start();

        return true;
    }

    public boolean requestHttpPost(final int callbackResNo, final String serviceId, final JSONObject params, final int pageNo, final int pageItemCount, boolean showProgress) {
        this.mShowProgress = showProgress;
        final ServiceResWrapper httpResWrapper = new ServiceResWrapper(mServerCallback, callbackResNo);

        new Thread(new Runnable() {
            public void run() {
                connectPost(serviceId, httpResWrapper, params, pageNo, pageItemCount);
            }
        }).start();

        return true;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void connectPost(final String serviceId, final ServiceResWrapper resWrapper, final JSONObject params, final int pageNo, final int pageNumb) {
        String reqServiceUrl = ServerInfo.SERVER_URL + ServerInfo.SERVER_PORT + ServerInfo.SERVER_CONTEXT + serviceId;

//		List<NameValuePair> arrReqData = addCommonInfo(params,serviceId,pageNo,pageNumb);
        String reqParam = makeParameters(params, serviceId, pageNo, pageNumb);

        LogHelper.i("=============================================");
        LogHelper.i("========   HttpURLConnect REQUEST   =========");
        LogHelper.i("=============================================");
        LogHelper.i("ServiceId : " + serviceId);
        LogHelper.i("reqServiceUrl : " + reqServiceUrl);
        LogHelper.i("reqParam : " + reqParam);
        LogHelper.i("=============================================");

        try {
            URL url = new URL(reqServiceUrl);
            mHttpUrlConn = (HttpURLConnection) url.openConnection();

            mHttpUrlConn.setRequestMethod(ServerInfo.SERVER_TYPE_POST);
            mHttpUrlConn.setConnectTimeout(ServerInfo.SERVER_TIME_OUT);
            mHttpUrlConn.setReadTimeout(ServerInfo.SERVER_TIME_OUT);
        } catch (IOException e) {
            LogHelper.printException(e);
            return;
        }

        //Header 값 셋팅
        if(ServerInfo.HTTP_PARAMETER_ACCEPT_JSON) {
            mHttpUrlConn.setRequestProperty("Accept", ServerInfo.SERVER_ACCEPT_TYPE_JSON);
            mHttpUrlConn.setRequestProperty("Content-Type", ServerInfo.SERVER_ACCEPT_TYPE_JSON);
        } else {
            mHttpUrlConn.setRequestProperty("Accept", ServerInfo.SERVER_ACCEPT_TYPE_URL_ENCODED);
            mHttpUrlConn.setRequestProperty("Content-Type", ServerInfo.SERVER_ACCEPT_TYPE_URL_ENCODED);
        }

        mHttpUrlConn.setRequestProperty("Accept-Charset", ServerInfo.SERVER_ACCEPT_CHARSET);
        mHttpUrlConn.setRequestProperty("Cache-Control", ServerInfo.SERVER_CACHE_STATE);

        mHttpUrlConn.setDoInput(true);
        mHttpUrlConn.setDoOutput(true);

        synchronized (mHttpUrlConn) {
            OutputStreamWriter osw = null;
            InputStream inputSteam = null;
            OutputStream os = null;

            try {
                os = mHttpUrlConn.getOutputStream();
                osw = new OutputStreamWriter(os, ServerInfo.SERVER_CHARSET);
                osw.write(reqParam);
                osw.flush();
                osw.close();
                os.close();

                int code = mHttpUrlConn.getResponseCode();

                if (code == HttpURLConnection.HTTP_OK) {
                    inputSteam = mHttpUrlConn.getInputStream();
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputSteam, ServerInfo.SERVER_CHARSET));


                    String buffer = "";
                    StringBuffer response = new StringBuffer();

                    while ((buffer = bufReader.readLine()) != null) {
                        response.append(buffer);
                    }
                    bufReader.close();
                    inputSteam.close();

                    resWrapper.setResponse(response.toString());
                } else {
                    inputSteam = mHttpUrlConn.getErrorStream();
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputSteam, ServerInfo.SERVER_CHARSET));

                    String buffer = "";
                    StringBuffer response = new StringBuffer();

                    while ((buffer = bufReader.readLine()) != null) {
                        response.append(buffer);
                    }
                    bufReader.close();
                    inputSteam.close();

                    resWrapper.setError(response.toString());
                }
            } catch (UnsupportedEncodingException e) {
                LogHelper.printException(e);
                resWrapper.setError(e);
            } catch (IOException e) {
                LogHelper.printException(e);
                resWrapper.setError(e);
            } finally {
                try {
                    if (osw != null) {
                        osw.close();
                    }

                    if (os != null) {
                        os.close();
                    }

                    if (inputSteam != null) {
                        inputSteam.close();
                    }
                } catch (IOException e1) {
                    LogHelper.e(e1);
                }

                if (mHttpUrlConn != null) {
                    mHttpUrlConn.disconnect();
                }

                LogHelper.i("connectPost finally");
            }
        }
        mHttpUrlConn.disconnect();

        Thread dataWorkTherd = new Thread(resWrapper);
        dataWorkTherd.start();
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String makeParameters(JSONObject jsonParams, String serviceId, int pageNo, int pageNumb) {

        JSONObject jsonParamAddCommon = addCommonParameter(jsonParams);

        if( ServerInfo.HTTP_PARAMETER_ACCEPT_JSON ) {
            return jsonParamAddCommon.toString();
        } else {
            return makeUriParameters(jsonParamAddCommon);
        }
    }

    private JSONObject addCommonParameter(JSONObject jsonParams) {
/*
        JSONObject returnJson = new JSONObject();
        try {
            /////////////////////////////////////////////////////////////////////////////
            // 공통 정보
            returnJson.put("BASEINFO_MAC", "00.00.00.00");
            returnJson.put("USER_ID", "TEST_ID");
            returnJson.put("REQ_DATA", jsonParams);

        } catch (JSONException e) {
            LogHelper.printException(e);
        }
*/
        return jsonParams;
    }

    private String makeUriParameters(JSONObject parameter) {
        Uri.Builder builder = new Uri.Builder();

        Iterator paramIterator = parameter.keys();
        if ( paramIterator != null ) {
            while (paramIterator.hasNext()) {
                String key = (String) paramIterator.next();
                builder.appendQueryParameter(key, UtilHelper.getJsonData(parameter,key));
            }
        }
        return builder.build().getEncodedQuery();
    }
}
