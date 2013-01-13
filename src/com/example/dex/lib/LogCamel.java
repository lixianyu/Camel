package com.example.dex.lib;

import android.util.Log;

public class LogCamel {

	public static void logv(String tag, String log) {
		Log.v(tag, "Camel: " + log);
	}

	public static void logd(String tag, String log) {
		Log.d(tag, "Camel: " + log);
	}

	public static void logi(String tag, String log) {
		Log.i(tag, "Camel: " + log);
	}

	public static void logw(String tag, String log) {
		Log.w(tag, "Camel: " + log);
	}

	public static void loge(String tag, String log) {
		Log.e(tag, "Camel: " + log);
	}

	public static void loge(String tag, String msg, Throwable tr) {
		Log.e(tag, msg, tr);
	}
}
