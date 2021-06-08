package com.qunar.im.base.util;

import android.util.Log;

import com.qunar.im.common.CommonConfig;

/**
 * @author zhaoliu
 */
public class LogUtil {
	private static final String LOG_TAG = "QIM";

	/** 详细信息 */
	public static void v(String msg) {
		v(LOG_TAG,msg);
	}
	public static void v(String tag,String msg) {
		if (CommonConfig.isDebug) {
			if (msg == null)
				msg = "value is null";
			Log.v(tag, msg);
		}
	}
	public static void v(String tag,String msg,Throwable tr) {
        if (CommonConfig.isDebug) {
			if (msg == null)
				msg = "value is null";
			Log.v(tag, msg, tr);
		}
	}
	
	/** 调试日志 */
	public static void d(String msg) {
		d(LOG_TAG,msg);
	}
	public static void d(String tag,String info) {
		if (CommonConfig.isDebug) {
			if (info == null)
				info = "value is null";
			Log.d(tag, info);
		}
	}
	public static void d(String tag,String info,Throwable tr) {
		if (CommonConfig.isDebug) {
			if (info == null)
				info = "value is null";
			Log.d(tag, info, tr);
		}
	}
	
	/** 信息日志 */
	public static void i(String info) {
		i(LOG_TAG, info);
	}
	public static void i(String tag,String info) {
		if (CommonConfig.isDebug) {
			if (info == null)
				info = "value is null";
			Log.i(tag, info);
		}
	}
	public static void i(String tag,String info,Throwable tr) {
		if (CommonConfig.isDebug) {
			if (info == null)
				info = "value is null";
			Log.i(tag, info, tr);
		}
	}


	/** 警告日志 */
	public static void w(String msg) {
		w(LOG_TAG,msg);
	}
	public static void w(String tag,String info) {
		if (CommonConfig.isDebug) {
			if (info == null)
				info = "value is null";
			Log.w(tag, info);
		}
	}
	public static void w(String tag,String info,Throwable tr) {
		if (CommonConfig.isDebug) {
			if (info == null)
				info = "value is null";
			Log.w(tag, info, tr);
		}
	}

	/** 错误日志 */
	public static void e(String msg) {
		e(LOG_TAG,msg);
	}
	public static void e(String tag,String info) {
		if (CommonConfig.isDebug) {
			if (info == null)
				info = "value is null";
			Log.e(tag, info);
		}
	}
	public static void e(String tag,String info,Throwable tr) {
		if (CommonConfig.isDebug) {
			if (info == null)
				info = "value is null";
			Log.e(tag, info, tr);
		}
	}
	
}
