/*
 Copyright 2012-2013, Polyvi Inc. (http://polyvi.github.io/openxface)
 This program is distributed under the terms of the GNU General Public License.

 This file is part of xFace.

 xFace is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 xFace is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with xFace.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.polyvi.xface.extension;

import java.io.File;
import java.util.Iterator;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.polyvi.xface.ams.XAppInstallListener;
import com.polyvi.xface.ams.XAppList;
import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.app.XIApplication;
import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.view.XAppWebView;

public class XAmsExt extends CordovaPlugin {

	/** AMS 提供给js用户的接口名字 */
	private static final String COMMAND_LIST_INSTALLED_APPLICATIONS = "listInstalledApplications";
	private static final String COMMAND_START_APPLICATION = "startApplication";
	private static final String COMMAND_UNINSTALL_APPLICATION = "uninstallApplication";
	private static final String COMMAND_INSTALL_APPLICATION = "installApplication";
	private static final String COMMAND_UPDATE_APPLICATION = "updateApplication";
	private static final String COMMAND_LIST_PRESET_APPLICATIONS = "listPresetAppPackages";
	private static final String COMMAND_GET_START_APP_INFO = "getStartAppInfo";

	/** 定义一些tag常量 */
	private static final String TAG_APP_ID = "appid";
	private static final String TAG_NAME = "name";
	private static final String TAG_BACKGROUND_COLOR = "icon_background_color";
	private static final String TAG_ICON = "icon";
	private static final String TAG_VERSION = "version";
	private static final String TAG_TYPE = "type";
	private static final String TAG_HEIGHT = "height";
	private static final String TAG_WIDTH = "width";

	/** 应用管理器 */
	private XAms mAms;

	public void init(XAms ams, CordovaInterface cordova, CordovaWebView webView) {
		mAms = ams;
		this.initialize(cordova, webView);
	}

	/**
	 * 安装app
	 * 
	 * @param webContext
	 *            调用该接口的应用对象
	 * @param packagePath
	 *            安装包路径
	 * @param callbackCtx
	 *            callback上下文环境
	 */
	private void installApplication(final CordovaWebView webContext,
			final String packagePath, final CallbackContext callbackCtx) {
		cordova.getThreadPool().execute(new Runnable() {
			public void run() {
				XAppInstallListener listener = new XAppInstallListener(
						callbackCtx);
				mAms.installApp(webContext, packagePath, listener);
			}
		});

	}

	/**
	 * 更新app
	 * 
	 * @param webContext
	 *            调用该接口的应用对象
	 * @param packagePath
	 *            更新包路径
	 * @param callbackCtx
	 *            js回调的上下文环境
	 */
	private void updateApplication(final CordovaWebView webContext,
			final String packagePath, final CallbackContext callbackCtx) {
		cordova.getThreadPool().execute(new Runnable() {
			public void run() {
				XAppInstallListener listener = new XAppInstallListener(
						callbackCtx);
				mAms.updateApp(webContext, packagePath, listener);
			}
		});

	}

	/**
	 * 卸载application
	 * 
	 * @param webContext
	 *            调用该接口的应用对象
	 * @param appId
	 *            需要卸载的应用id
	 * @param callbackCtx
	 *            js回调上下文环境
	 */
	private void uninstallApplication(final CordovaWebView webContext, final String appId,
			final CallbackContext callbackCtx) {
		cordova.getThreadPool().execute(new Runnable() {
			public void run() {
				XAppInstallListener listener = new XAppInstallListener(
						callbackCtx);
				mAms.uninstallApp(appId, listener);
			}
		});

	}

	/**
	 * 启动一个应用程序
	 * 
	 * @param appId
	 *            启动应用的id
	 */
	private void startApplication(CordovaWebView webContext, String appId,
			String params, CallbackContext callbackCtx) {
		final String fAppId = appId;
		final CallbackContext cbContext = callbackCtx;
		final String fParams = params;
		this.cordova.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				// 由于会操作到UI，确保在UI线程中执行
				boolean sucessful = mAms.startApp(fAppId, fParams);
				if (sucessful) {
					cbContext.success(fAppId);
				} else {
					cbContext.error(fAppId);
				}
			}
		});
	}

	/**
	 * 列出系统已经安装过的应用程序
	 * 
	 * @return 通过json数组的形式返回
	 */
	private JSONArray listInstalledApplication() {
		JSONArray result = new JSONArray();
		XAppList appList = mAms.getAppList();
		Iterator<XIApplication> appIterator = appList.iterator();
		while (appIterator.hasNext()) {
			JSONObject obj = translateAppInfoToJson(appIterator.next());
			result.put(obj);
		}
		return result;
	}

	private JSONObject translateAppInfoToJson(XIApplication app) {
		JSONObject obj = new JSONObject();
		try {
			obj.put(TAG_APP_ID, app.getAppInfo().getAppId());
			obj.put(TAG_NAME, app.getAppInfo().getName());
			obj.put(TAG_BACKGROUND_COLOR, app.getAppInfo()
					.getIconBackgroudColor());
			obj.put(TAG_ICON, app.getAppIconUrl());
			obj.put(TAG_VERSION, app.getAppInfo().getVersion());
			obj.put(TAG_TYPE, app.getAppInfo().getType());
			obj.put(TAG_WIDTH, app.getAppInfo().getWidth());
			obj.put(TAG_HEIGHT, app.getAppInfo().getHeight());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	private XApplication getApp() {
		XAppWebView webView = (XAppWebView) this.webView;
		return webView.getOwnerApp();
	}

	/**
	 * 列出预置安装包，每一项是预制安装包的路径，该路径是在默认app的workspace下面的pre_set目录中
	 * */
	private JSONArray listPresetAppPackages() {

		String[] presetApps = mAms
				.getPresetAppPackages(getApp().getWorkSpace());
		JSONArray presetAppsJsonArray = new JSONArray();
		if (null != presetApps) {
			for (String presetAppName : presetApps) {
				presetAppsJsonArray.put(XConstant.PRE_SET_APP_PACKAGE_DIR_NAME
						+ File.separator + presetAppName);
			}
		}
		return presetAppsJsonArray;
	}

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		if (action.equals(COMMAND_INSTALL_APPLICATION)) {
			String packagePath = args.getString(0);
			installApplication(webView, packagePath, callbackContext);
			return true;
		} else if (action.equals(COMMAND_UNINSTALL_APPLICATION)) {
			String appId = args.getString(0);
			uninstallApplication(webView, appId, callbackContext);
			return true;
		} else if (action.equals(COMMAND_START_APPLICATION)) {
			String appId = args.getString(0);
			String params = getStartParams(args);
			startApplication(webView, appId, params, callbackContext);
			return true;
		} else if (action.equals(COMMAND_LIST_INSTALLED_APPLICATIONS)) {
			JSONArray apps = listInstalledApplication();
			callbackContext.success(apps);
			return true;
		} else if (action.equals(COMMAND_UPDATE_APPLICATION)) {
			String packagePath = args.getString(0);
			updateApplication(webView, packagePath, callbackContext);
			return true;
		} else if (COMMAND_LIST_PRESET_APPLICATIONS.equals(action)) {
			cordova.getThreadPool().execute(new Runnable(){
				public void run(){
					JSONArray presetApps = listPresetAppPackages();
					callbackContext.success(presetApps);
				}
			});
			return true;
			
		} else if (action.equals(COMMAND_GET_START_APP_INFO)) {
			JSONObject json = translateAppInfoToJson(getApp());
			callbackContext.success(json);
			return true;
		}
		return false;
	}

	private String getStartParams(JSONArray args) throws JSONException {
		String params = "";
		int argLen = args.length();
		if (argLen == 2) {
			params = args.getString(1);
		}
		return params;
	}

}
