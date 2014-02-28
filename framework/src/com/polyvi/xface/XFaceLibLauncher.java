package com.polyvi.xface;

import android.app.Activity;
import android.content.Intent;

import com.polyvi.xface.util.XConstant;

public class XFaceLibLauncher {
	public static final void startXface(Activity activity, String params) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.XFACELIBLAUNCHER");
		if (null != params) {
			params = params.replaceAll("'", "\\\\'");
			params = params.replaceAll("\"", "\\\\\"");
			intent.putExtra(XConstant.TAG_APP_START_PARAMS, params);
		}
		activity.startActivity(intent);
	}
}