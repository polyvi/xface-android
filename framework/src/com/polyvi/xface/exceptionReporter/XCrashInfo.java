
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

package com.polyvi.xface.exceptionReporter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.polyvi.xface.util.XDeviceInfo;
import com.polyvi.xface.util.XLog;


public class XCrashInfo {
    private static final String CLASS_NAME = XCrashInfo.class.getSimpleName();
    /**exception 信息标示符*/
    private static final String TAG_EXCEPTION_INFO= "exceptionInfo";
    private static final String TAG_EXCEPTION_NAME = "exceptionName";
    private static final String TAG_EXCEPTION_REASON = "exceptionReason";
    private static final String TAG_STACK_TRACE = "stackTrace";
    /**设备信息标示符*/
    private static final String TAG_DEVICE_INFO = "deviceInfo";
    /**设备信息*/
    private JSONObject mDeviceInfo;
    /**异常信息*/
    private JSONObject mExceptionInfo;
    /**包含设备信息和异常信息的crashInfo*/
    private JSONObject mCrashInfo;

    public XCrashInfo(Throwable ex, Context context) {
        mDeviceInfo = getDeviceInfo(context);
        mExceptionInfo =  getExceptionInfo(ex);
        mCrashInfo = getJSONCrashInfo();
    }

    /**
     * 获得异常的栈信息
     * @param ex
     * @return 返回异常栈信息
     */
    private JSONObject getExceptionInfo(Throwable ex) {
         JSONObject exceptionInfo = new JSONObject();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         PrintStream printStream = new PrintStream(baos);
         ex.printStackTrace(printStream);
         byte[] data = baos.toByteArray();
         String info = new String(data);
         Throwable cause = ex.getCause();
         try {
            exceptionInfo.put(TAG_EXCEPTION_NAME, ex.getClass().getSimpleName());
            exceptionInfo.put(TAG_EXCEPTION_REASON, null == cause ? "unknown cause" : cause.toString());
            exceptionInfo.put(TAG_STACK_TRACE, info);
        } catch (JSONException e) {
            XLog.e(CLASS_NAME, "JSONException:", e.getMessage());
            return null;
        }
        return exceptionInfo;
    }

    /**
     * 获取设备信息,初始化mDeviceInfo
     * @return 设备信息
     */
    private JSONObject getDeviceInfo(Context context) {
        JSONObject deviceInfo = new JSONObject();
        HashMap<String, Object> basedeviceInfo = new XDeviceInfo().getBaseDeviceInfo(context);
        try {
            if (null != basedeviceInfo) {
                Iterator<Entry<String, Object>> iter = basedeviceInfo.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, Object> entry = iter.next();
                    deviceInfo.put(entry.getKey(), (String) entry.getValue());
                }
            }
        } catch (JSONException e) {
            XLog.e(CLASS_NAME, "JSONException:", e);
            return null;
        }
        return deviceInfo;
    }

    /**
     * 获取JSON格式的CrashInfo
     * @return 设备信息
     */
    private JSONObject getJSONCrashInfo() {
        JSONObject crashInfo = new JSONObject();
        try {
            crashInfo.put(TAG_DEVICE_INFO, null == mDeviceInfo ? "unknown deviceInfo" : mDeviceInfo);
            crashInfo.put(TAG_EXCEPTION_INFO, null == mExceptionInfo ? "unknown exceptionInfo" : mExceptionInfo);
        } catch (JSONException e) {
            XLog.e(CLASS_NAME, "JSONException:", e);
            return null;
        }
        return crashInfo;
    }

    @Override
    public String toString() {
        return mCrashInfo.toString();
    }

    public JSONObject getCrashInfo() {
        return mCrashInfo;
    }
}
