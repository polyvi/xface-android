/*
 This file was modified from or inspired by Apache Cordova.

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License. You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied. See the License for the
 specific language governing permissions and limitations
 under the License.
 */

package com.polyvi.xface.view;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Pair;
import android.webkit.JsPromptResult;
import android.webkit.WebView;

import com.polyvi.xface.XFaceMainActivity;
import com.polyvi.xface.event.XEvent;
import com.polyvi.xface.event.XEventType;
import com.polyvi.xface.event.XSystemEventCenter;
import com.polyvi.xface.util.XLog;

/**
 * 主要负责实现webview提供的回调函数
 *
 */
public class XWebChromeClient extends CordovaChromeClient {

	private CordovaInterface mInterface;

	private static final String CLASS_NAME = XWebChromeClient.class
			.getSimpleName();

	/**
	 * Constructor.
	 *
	 * @param cordova
	 */
	public XWebChromeClient(CordovaInterface cordova) {
		super(cordova);
		mInterface = cordova;
	}

	/**
	 * Constructor.
	 *
	 * @param ctx
	 * @param app
	 */
	public XWebChromeClient(CordovaInterface ctx, CordovaWebView app) {
		super(ctx, app);
		mInterface = ctx;
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message,
			String defaultValue, JsPromptResult result) {
		boolean reqOk = false;
		if (url.startsWith("file://") || Config.isUrlWhiteListed(url)) {
			reqOk = true;
		}
		if (reqOk && defaultValue != null
				&& defaultValue.equals("xFace_close_application:")) {
			XAppWebView appView = ((XAppWebView) view);
			int viewId = appView.getViewId();
			XEvent evt = XEvent.createEvent(XEventType.CLOSE_APP, viewId);
			((XFaceMainActivity) mInterface.getActivity()).getEventCenter()
					.sendEventSync(evt);
			result.confirm("");
			return true;
		} else if (reqOk && defaultValue != null
				&& defaultValue.equals("xFace_app_send_message:")) {
			try {
				JSONArray args = new JSONArray(message);
				Pair<XAppWebView, String> appMessage = new Pair<XAppWebView, String>(
						(XAppWebView) view, args.getString(0));
				XEvent evt = XEvent.createEvent(XEventType.XAPP_MESSAGE,
						appMessage);
				((XFaceMainActivity) mInterface.getActivity()).getEventCenter()
				.sendEventSync(evt);
			} catch (JSONException e) {
				XLog.e(CLASS_NAME, "");
				XLog.e(CLASS_NAME, e.getMessage());
			}
			result.confirm("");
			return true;
		}
		return super.onJsPrompt(view, url, message, defaultValue, result);
	}
}
