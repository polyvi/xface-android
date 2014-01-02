
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

package com.polyvi.xface.ams;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import com.polyvi.xface.ams.XAMSError.AMS_ERROR;

/**
 * app的安装监听器类，负责监听app的安装进度及安装结果状态，并封装成消息，发送给mInstallHandler进行处理
 */
public class XAppInstallListener implements XInstallListener {

    private final static String TAG_APP_ID = "appid";
    private final static String TAG_INSTALL_PROGRESS = "progress";
    private final static String TAG_ERROR_CODE = "code";
    private final static String TAG_OPERATION_TYPE = "type";
    private final static String TAG_CALLBACK = "callback";

    /** js回调上下文环境 */
    private CallbackContext mCallbackCtx;

    public XAppInstallListener(CallbackContext callbackCtx) {
        mCallbackCtx = callbackCtx;
    }

    @Override
    public void onProgressUpdated(AMS_OPERATION_TYPE type,
            InstallStatus progressState) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(TAG_INSTALL_PROGRESS, progressState.ordinal());
            jsonObj.put(TAG_OPERATION_TYPE, type.ordinal());
            jsonObj.put(TAG_CALLBACK, "progress");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(
        		PluginResult.Status.OK, jsonObj);
        // 在安装过程中，执行js回调后，由于成功或者失败还需执行回调，故这不能删除js设置的回调
        result.setKeepCallback(true);
        mCallbackCtx.sendPluginResult(result);
    }

    @Override
    public void onError(AMS_OPERATION_TYPE type, String appId,
            AMS_ERROR errorState) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(TAG_ERROR_CODE, errorState.ordinal());
            jsonObj.put(TAG_OPERATION_TYPE, type.ordinal());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCallbackCtx.error(jsonObj);
    }

    @Override
    public void onSuccess(AMS_OPERATION_TYPE type, String appId) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(TAG_APP_ID, appId);
            jsonObj.put(TAG_OPERATION_TYPE, type.ordinal());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCallbackCtx.success(jsonObj);
    }
}
