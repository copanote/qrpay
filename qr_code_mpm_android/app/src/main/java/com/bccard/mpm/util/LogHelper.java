/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : LogHelper
 * 작성자명   : 20170448
 * 상세설명   : Log 관련 Helper 클래스
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

import android.content.Context;
import android.util.Log;
import com.bccard.mpm.BuildConfig;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogHelper {
	//운영 빌드전 확인  운영:false , 개발 : true
	public final static boolean DEV_MODE =  BuildConfig.DEBUG_MODE;;
	public final static String G_TAG = "mpm";

	public static void i (Object... messages) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();
			
			Log.i( G_TAG, "["+getClassName(className)+"] "+methodName+"("+lineNumber+")"+" : "+ getLogMessage(messages) );
		}
	}

	public static void e (Object... messages) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

			Log.e( G_TAG, "["+getClassName(className)+"] "+methodName+"("+lineNumber+")"+" : "+ getLogMessage(messages) );
		}
	}

	public static void w (Object... messages) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

			Log.w( G_TAG, "["+getClassName(className)+"] "+methodName+"["+lineNumber+"]"+" : "+ getLogMessage(messages) );
		}
	}

	public static void v (Object... messages) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();
			
			Log.v( G_TAG, "["+getClassName(className)+"] "+methodName+"["+lineNumber+"]"+" : "+ getLogMessage(messages) );
		}
	}
	
	public static void d (Object... messages) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();
			
			Log.d( G_TAG, "["+getClassName(className)+"] "+methodName+"["+lineNumber+"]"+" : "+ getLogMessage(messages) );
		}
	}
	
	public static void i (Context context, Object... messages) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();
			
			Log.i( context.getClass().getSimpleName(), "["+getClassName(className)+"] "+methodName+"["+lineNumber+"]"+" : "+ getLogMessage(messages) );
		}
	}
	
	public static void e (Context context, Object... messages) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();
			
			Log.e( context.getClass().getSimpleName(), "["+getClassName(className)+"] "+methodName+"["+lineNumber+"]"+" : "+ getLogMessage(messages) );
		}
	}

	public static void w (Context context, Object... messages) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

			Log.w( context.getClass().getSimpleName(), "["+getClassName(className)+"] "+methodName+"["+lineNumber+"]"+" : "+ getLogMessage(messages) );
		}
	}

	public static void v (Context context, Object... messages) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();
			
			Log.v( context.getClass().getSimpleName(), "["+getClassName(className)+"] "+methodName+"["+lineNumber+"]"+" : "+ getLogMessage(messages) );
		}
	}
	
	public static void d (Context context, Object... messages) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();
			
			Log.d( context.getClass().getSimpleName(), "["+getClassName(className)+"] "+methodName+"["+lineNumber+"]"+" : "+ getLogMessage(messages) );
		}
	}

	public static void printException (Throwable t) {
		if (DEV_MODE) {
			String className = new Throwable().getStackTrace()[1].getClassName();
			String methodName = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

			StringWriter errorWriter = new StringWriter();
			t.printStackTrace(new PrintWriter(errorWriter));

			Log.e( G_TAG, "["+getClassName(className)+"] "+methodName+"["+lineNumber+"]"+" Error : "+ t.getClass().getName() + "\n" + errorWriter);
		}
	}

	public static String getLogMessage(Object... messages) {
		StringBuilder sbLogMessage = new StringBuilder();
		if (messages != null) {
			int index = 0;
			for (Object msg : messages) {
				sbLogMessage.append(msg);

				if(messages.length > 1 && index < messages.length) {
					sbLogMessage.append("\n");
				}
				index++;
			}

			return sbLogMessage.toString();
		}
		return "Not Message";
	}

	public static String getClassName(String classPackage) {
		if (classPackage != null && !classPackage.isEmpty() ) {
			int dotIndex = classPackage.lastIndexOf(".");
			if ((dotIndex + 1) < classPackage.length()) {
				return classPackage.substring(dotIndex + 1);
			} else {
				return classPackage;
			}
		} else {
			return "";
		}
	}
}