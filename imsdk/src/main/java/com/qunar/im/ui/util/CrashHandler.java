package com.qunar.im.ui.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.common.CurrentPreference;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告
 * 
 * @author YINZHIPING
 * @version 1.0, 2012-10-16 下午01:55:08
 * 
 * @(#)CrashHandler.java
 * 
 * @Copyright 2012 AngelShine
 */
@SuppressLint("SimpleDateFormat")
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	private static final boolean isUploadError = true;
	// 系统默认的UncaughtException处理类
	private UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private final Map<String, String> infos = new HashMap<String, String>();
	
	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Logger.i("崩溃异常：" + e.getMessage());
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
//		new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();
//				Looper.loop();
//			}
//		}.start();
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfoile(ex);
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
			infos.put("userid", CurrentPreference.getInstance().getUserid());
			infos.put("domain", QtalkNavicationService.getInstance().getXmppdomain());
			infos.put("navurl", DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, ""));
		} catch (NameNotFoundException e) {
			Logger.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Logger.i("崩溃参数信息：" + field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Logger.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfoile(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat logFt = new SimpleDateFormat("yyyyMMdd");
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key.equals("TIME")) {
				Date d = new Date(Long.valueOf(value + ""));
				sb.append(key + "=" + format.format(d) + "\n");
			} else {
				sb.append(key + "=" + value + "\n");
			}
		}

		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String t = s.format(new Date());
		sb.append("CurrentTime = " + t + "\n");

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		Logger.i("崩溃异常信息：" + sb.toString());

		return null;
	}
}