
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
import org.json.JSONException;
import org.json.JSONObject;

import com.polyvi.xface.ams.XAMSError.AMS_ERROR;

public class XAppStartListenerImp implements XAppStartListener {

    private final static String TAG_APP_ID = "appid";
    private final static String TAG_ERROR_CODE = "code";

    /** js回调上下文环境 */
    private CallbackContext mCallbackCtx;

    public XAppStartListenerImp(CallbackContext callbackCtx) {
        mCallbackCtx = callbackCtx;
    }

    @Override
    public void onError(String appId, AMS_ERROR errorState) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(TAG_APP_ID, appId);
            jsonObj.put(TAG_ERROR_CODE, errorState.ordinal());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCallbackCtx.error(jsonObj);

    }

    @Override
    public void onSuccess(String appId) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(TAG_APP_ID, appId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCallbackCtx.success(jsonObj);
    }

}
