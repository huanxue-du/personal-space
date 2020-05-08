package com.autolink.radio55.utils;
import android.util.Log;

public class ELog {
	private static String TAG = "ALRadio"; // 程序主TAG

	private static final int VERBOSE = 0x00000001;
	private static final int DEBUG = 0x00000010;
	private static final int INFO = 0x00000100;
	private static final int WARN = 0x00001000;
	private static final int ERROR = 0x00010000;

	private static final int OUTPUT_NONE = 0;
	private static final int OUTPUT_ALL = VERBOSE | DEBUG | INFO | WARN | ERROR;
	private static final int OUTPUT_ERROOR = INFO;

	private static final int OUTPUT_PRIORITY = OUTPUT_ALL; // 开发阶段设置为OUTPUT_ALL，定版后设置OUTPUT_NONE
															// 关闭日志

	private static void log(int priority, String tag, String msg) {
		if ((OUTPUT_PRIORITY & priority) != 0) {
			String className = Thread.currentThread().getStackTrace()[4]
					.getClassName();
			String clazz = className.substring(className.lastIndexOf(".") + 1); // 获取类名
			tag = tag != null ? tag : TAG; // 如果子TAG为空，设置为程序主TAG
			msg = "[" + clazz + "] " + msg;
			switch (priority) {
			case VERBOSE:
				Log.v(tag, msg);
				break;
			case DEBUG:
				Log.d(tag, msg);
				break;
			case INFO:
				Log.i(tag, msg);
				break;
			case WARN:
				Log.w(tag, msg);
				break;
			case ERROR:
				Log.e(tag, msg);
				break;
			default:
				break;
			}
		}
	}

	public static void v(String msg) {
		log(VERBOSE, null, msg);
	}

	public static void d(String msg) {
		log(DEBUG, null, msg);
	}

	public static void i(String msg) {
		log(INFO, null, msg);
	}

	public static void w(String msg) {
		log(WARN, null, msg);
	}

	public static void e(String msg) {
		log(ERROR, null, msg);
	}

	public static void v(String tag, String msg) {
		log(VERBOSE, tag, msg);
	}

	public static void d(String tag, String msg) {
		log(DEBUG, tag, msg);
	}

	public static void i(String tag, String msg) {
		log(INFO, tag, msg);
	}

	public static void w(String tag, String msg) {
		log(WARN, tag, msg);
	}

	public static void e(String tag, String msg) {
		log(ERROR, tag, msg);
	}

	public static void d(Throwable t) {
		log(DEBUG, null, Log.getStackTraceString(t));
	}

}
