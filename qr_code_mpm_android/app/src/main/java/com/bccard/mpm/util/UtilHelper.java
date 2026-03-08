/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : UtilHelper
 * 작성자명   : 20170448
 * 상세설명   : 기본 Util 관련 Helper 클래스
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

package com.bccard.mpm.util;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import com.bccard.mpm.R;
import com.bccard.mpm.common.Constant;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UtilHelper {
    public final static int TYPE_WIFI = 1;
    public final static int TYPE_MOBILE = 2;
    public final static int TYPE_NOT_CONNECTED = 0;

    public static int getVersionCode(Context context) {
        PackageInfo pi = null;
        int versionCode = 0;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogHelper.printException(e);
        }

        return versionCode;
    }

    public static String getVersionName(Context context) {
        PackageInfo pi = null;
        String versionName = "";
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogHelper.printException(e);
        }

        return versionName;
    }

    public static String getJsonData(JSONObject jsonData, String key ) {
        try {
            return jsonData.getString(key);
        } catch (JSONException e) {
            LogHelper.printException(e);
            return "";
        }
    }

    public static void excuteFile(Context context, String filePath) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        File file = new File(filePath);
        String fileExtend = FileHelper.getFileExtensionName(filePath);
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName(), file);

        if (fileExtend.equals("mp3")) {
            intent.setDataAndType(uri, "audio/*");
        } else if (fileExtend.equals("mp4")) {
            intent.setDataAndType(uri, "vidio/*");
        } else if (fileExtend.equals("jpg") || fileExtend.equals("jpeg")
                || fileExtend.equals("JPG") || fileExtend.equals("gif")
                || fileExtend.equals("png") || fileExtend.equals("bmp")) {
            intent.setDataAndType(uri, "image/*");
        } else if (fileExtend.equals("txt")) {
            intent.setDataAndType(uri, "text/*");
        } else if (fileExtend.equals("doc") || fileExtend.equals("docx")) {
            intent.setDataAndType(uri, "application/msword");
        } else if (fileExtend.equals("xls") || fileExtend.equals("xlsx")) {
            intent.setDataAndType(uri,"application/vnd.ms-excel");
        } else if (fileExtend.equals("ppt") || fileExtend.equals("pptx")) {
            intent.setDataAndType(uri,"application/vnd.ms-powerpoint");
        } else if (fileExtend.equals("pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        }

        context.startActivity(intent);
    }

    // Foreground 여부 확인
    public static boolean isRunningProcess(Context context) {
        ActivityManager actMng = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = actMng.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = actMng.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            return componentInfo.getPackageName().equals(context.getPackageName());
        }

        return false;
    }

    public static String getFrontActName(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
            ComponentName currentActivity = tasks.get(0).topActivity;

            String className = currentActivity.getClassName();
            if (className.length() > 1) {
                int beginIndex = className.lastIndexOf(".") + 1;

                if (beginIndex <= className.length()) {
                    return className.substring(beginIndex);
                } else {
                    return className;
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            LogHelper.printException(e);
            return "";
        }
    }

    // 최상위 Activity 확인
    public static String getFrontActivity(Context context) {
        try {
            if (Build.VERSION.SDK_INT < 21) {
                return (getFrontActPreLollipop(context))[0];
            } else {
                return (getFrontActAfterLollipop(context))[0];
            }
        } catch (Exception e) {
            LogHelper.printException(e);
            return null;
        }
    }

//  @SuppressWarnings("deprecation")
    public static String[] getFrontActPreLollipop(Context context) throws Exception {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        ComponentName currentActivity = tasks.get(0).topActivity;
        return new String[] { currentActivity.getPackageName() };
    }

    public static String[] getFrontActAfterLollipop(Context context) throws Exception {
        final Set<String> activePackages = new HashSet<String>();
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        final List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            activePackages.addAll(Arrays.asList(processInfo.pkgList));
            LogHelper.d("processName = " + processInfo.processName + ", importance = " + processInfo.importance);

            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }

    public static Boolean isConntectNetWork(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo networkMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo networkWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            boolean isMobileAvailable = false;
            boolean isMobileConnect = false;
            boolean isWifiAvailable = false;
            boolean isWifiConnect = false;

            if (networkMobile != null ) {
                isMobileAvailable = networkMobile.isAvailable();
                isMobileConnect = networkMobile.isConnectedOrConnecting();
            }

            if (networkWifi != null ) {
                isWifiAvailable = networkWifi.isAvailable();
                isWifiConnect = networkWifi.isConnectedOrConnecting();
            }

            return (isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect);
        }

        return false;
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectManager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static boolean isJSONValid(String jsonInString) {
        try {
            new Gson().fromJson(jsonInString, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    public static String getParsingCharValData(String fullString) {
        if (fullString != null && !fullString.isEmpty()) {
            String charValue = "";

            if (isJSONValid(fullString)) {
                LogHelper.i("getParsingCharValData isJSONValid");
                try {
                    JSONObject jsonData = new JSONObject(fullString);
                    charValue = getJsonData(jsonData, "l").replaceAll(";","");
                } catch (JSONException e) {
                    charValue = fullString.replaceAll(";","");
                }
            } else {
                LogHelper.i("getParsingCharValData isJSONValid Not");
                charValue = fullString.replaceAll(";","");
            }

            return charValue;
        }

        return "";
    }

    public static String parsingReqSeqNoFromCharValData(String fullString) {
        LogHelper.i("parsingReqSeqNoFromCharValData fullString : " + fullString);

        String charValue = getParsingCharValData(fullString);
        if (charValue != null && !charValue.isEmpty()) {
            LogHelper.i("parsingReqSeqNoFromCharValData charValue : " + charValue);

            String [] split = charValue.split("!");
            if (split.length > 1) {
                LogHelper.w("parsingReqSeqNoFromCharValData return : " + split[1]);
                return split[1];
            } else {
                return "";
            }
        }

        return "";
    }

    public static String parsingCharValFromCharValData(String fullString) {
        LogHelper.i("parsingCharValFromCharValData fullString : " + fullString);

        String charValue = getParsingCharValData(fullString);
        if (charValue != null && !charValue.isEmpty()) {
            LogHelper.i("parsingCharValFromCharValData charValue : " + charValue);

            String [] split = charValue.split("!");
            if (split.length > 0) {
                LogHelper.w("parsingCharValFromCharValData return : " + split[0]);
                return split[0];
            } else {
                return "";
            }
        }

        return "";
    }

    public static String parsingPushMoveUrl(String fullString) {
        LogHelper.i("parsingPushMoveUrl fullString : " + fullString);

        String charValue = getParsingCharValData(fullString);
        if (charValue != null && !charValue.isEmpty()) {
            LogHelper.i("parsingPushMoveUrl charValue : " + charValue);

            String [] split = charValue.split("!");
            if (split.length > 0) {
                int lastIndex = split[0].lastIndexOf("//");

                if (split[0].length() > lastIndex+2) {
                    LogHelper.w("parsingPushMoveUrl return : " + split[0].substring(lastIndex+2));
                    return split[0].substring(lastIndex+2);
                } else {
                    return "";
                }
            } else {
                return "";
            }
        }

        return "";
    }

    public static HashMap<String,String> parsingPushMSG(String fullString) {
        HashMap<String,String> retMap = new HashMap<>();
        LogHelper.i("parsingPushMoveUrl fullString : " + fullString);
        if(fullString.length()>0){
            String[] splitStrings = fullString.split("[&]");
            for (String splitString : splitStrings) {
                String tempString = "";
                if (splitString.contains("?")) {
                    tempString = splitString.substring(splitString.indexOf("?") + 1);
                } else {
                    tempString = splitString;
                }
                if (tempString.contains("=")) {
                    int delIndex = tempString.indexOf("=");
                    String key = tempString.substring(0, delIndex);
                    String value = "";
                    if (tempString.length() > delIndex) {
                        value = tempString.substring(delIndex + 1);
                    }
                    retMap.put(key, value);
                }
            }
        }
        return retMap;
    }


    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showAlert(Context context, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                    }
                })
                .create()
                .show();
    }

    public static boolean permissionCheck(Context context, ArrayList<String> arrCheckPermission) {
        if(arrCheckPermission.size() == 0){
            return true;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ArrayList<String> arrDeniedPermission = new ArrayList<String>();

            int permissionCheck = 0;
            for(String permission:arrCheckPermission) {
                permissionCheck = ContextCompat.checkSelfPermission(context, permission);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    LogHelper.i("Permission(GRANTED) : " + permission);
                    arrDeniedPermission.add(permission);
                } else {
                    LogHelper.i( "Permission(DENIED) : " + permission);
                }
            }

            return arrDeniedPermission.size() <= 0;
        }

        return true;
    }

    public static boolean permissionCheckAndRequest(Context context, ArrayList<String> arrCheckPermission, int requestCode) {
        if(arrCheckPermission.size() == 0){
            return true;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ArrayList<String> arrDeniedPermission = new ArrayList<String>();

            int permissionCheck = 0;
            for(String permission:arrCheckPermission) {
                permissionCheck = ContextCompat.checkSelfPermission(context, permission);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    LogHelper.i("Permission(GRANTED) : " + permission);
                    arrDeniedPermission.add(permission);
                } else {
                    LogHelper.i( "Permission(DENIED) : " + permission);
                }
            }

            if (arrDeniedPermission.size() > 0) {
                String[] strPermission = new String[arrDeniedPermission.size()];
                for (int i=0; i<arrDeniedPermission.size(); i++) {
                    strPermission[i] = arrDeniedPermission.get(i);
                }
                ActivityCompat.requestPermissions((Activity) context, strPermission, requestCode);

                return false;
            }
        }

        return true;
    }


    public static void moveSystemSettingAppDetails(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }


    public static void moveSystemSettingAppDetailsForResult(Activity activity) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, Constant.REQAC_PERMISSION_APP_SETTING);
    }

    public static String getReformmattedDateString(String beforeString, String beforePattern, String afterPattern) {
        String formattedString = "";
        try{
            Date date = new SimpleDateFormat(beforePattern).parse(beforeString);
            SimpleDateFormat sdf = new SimpleDateFormat(afterPattern);
            formattedString = sdf.format(date);
        }catch(ParseException pe){

        }
        return formattedString;
    }

}