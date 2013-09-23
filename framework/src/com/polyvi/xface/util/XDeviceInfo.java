
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

package com.polyvi.xface.util;

import java.util.HashMap;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.polyvi.xface.core.XConfiguration;

public class XDeviceInfo {
    private static final String CLASS_NAME = XDeviceInfo.class.getSimpleName();
    private static final String PLATFORM = "Android"; // Device OS
     /**
     * 获取device相关信息
     */
    public HashMap<String, Object> getDeviceInfo(Context context) {
        HashMap<String, Object> deviceInfo = getBaseDeviceInfo(context);
        deviceInfo.put("width", this.getWidthPixels(context));
        deviceInfo.put("height", this.getHeightPixels(context));
        deviceInfo.put("isCameraAvailable", this.isCameraAvailable(context));
        deviceInfo.put("isCompassAvailable", this.isCompassAvailable(context));
        deviceInfo.put("isAccelerometerAvailable", this.isAccelerometerAvailable(context));
        deviceInfo.put("isTelephonyAvailable", this.isTelephonyAvailable(context));
        deviceInfo.put("isSmsAvailable", this.isSmsAvailable(context));
        deviceInfo.put("isFrontCameraAvailable", this.isFrontCameraAvailable(context));
        deviceInfo.put("isLocationAvailable", this.isLocationAvailable(context));
        deviceInfo.put("isWiFiAvailable", this.isWiFiAvailable(context));
        return deviceInfo;
    }

    /**
    * 获取device相关信息,用于crash报告
    */
   public HashMap<String, Object> getBaseDeviceInfo(Context context) {
       HashMap<String, Object> deviceInfo = new HashMap<String, Object>();
       try {
           deviceInfo.put("uuid", this.getUuid(context));
           deviceInfo.put("imei", this.getImei(context));
           deviceInfo.put("imsi", this.getImsi(context));
           deviceInfo.put("version", this.getOSVersion());
           deviceInfo.put("platform", PLATFORM);
           deviceInfo.put("name", this.getProductName());
           deviceInfo.put("xFaceVersion", getXFaceVersion());
           deviceInfo.put("model", this.getModel());
           PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
           deviceInfo.put("productVersion", info.versionName);
       }catch (NameNotFoundException exception) {
           XLog.e(CLASS_NAME, exception.getMessage(),exception);
           deviceInfo.put("productVersion", "unkown productVersion");
       }
       return deviceInfo;
   }

    /**
     * 获得xFace的版本号
     */
    private String getXFaceVersion() {
        return XConfiguration.getInstance().readEngineVersion();
    }

    /**
     * 获得device的product name
     */
    private String getProductName() {
        return android.os.Build.PRODUCT;
    }

    /**
     * 获得device的os version
     */
    private String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获得device的Universally Unique Identifier (UUID).
     */
    private String getUuid(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取device的International Mobile Equipment Identity(IMEI)
     */
    private String getImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取device的设备型号(model)
     */
    private String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取device的国际移动用户识别码(IMSI)
     */
    private String getImsi(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return XStringUtils.isEmptyString(tm.getSubscriberId()) ? "" : tm.getSubscriberId();
    }

    /**
     * 获取设备的DisplayMetrics
     *
     * @return 设备的DisplayMetrics
     */
    private DisplayMetrics getDisplayMetrics(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics();
    }

    /**
     * 获取设备的屏幕高度
     *
     * @return 设备的屏幕高度
     */
    private int getHeightPixels(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * 获取设备的屏幕宽度
     *
     * @return 设备的屏幕宽度
     */
    private int getWidthPixels(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 判断照相机功能是否可用
     * @return true:可用，false：不可用
     */
    private boolean isCameraAvailable(Context context) {
        return  context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    /**
     * 判断指南针功能是否可用
     *
     * @return true:可用，false：不可用
     */
    private boolean isCompassAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_SENSOR_COMPASS);
    }

    /**
     * 判断加速度计功能是否可用
     *
     * @return true:可用，false：不可用
     */
    private boolean isAccelerometerAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_SENSOR_ACCELEROMETER);
    }

    /**
     * 判断电话功能是否可用
     *
     * @return true:可用，false：不可用
     */
    private boolean isTelephonyAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_TELEPHONY);
    }

    /**
     * 判断短信功能是否可用
     *
     * @return true:可用，false：不可用
     */
    private boolean isSmsAvailable(Context context) {
        return isTelephonyAvailable(context);
    }

    /**
     * 判断前置摄像头功能是否可用
     *
     * @return true:可用，false：不可用
     */
    private boolean isFrontCameraAvailable(Context context) {
        // 低于2.3的原生系统都不支持前置摄像头
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
            return false;
        }
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT);
    }

    /**
     * 判断定位功能是否可用
     *
     * @return true:可用，false：不可用
     */
    private boolean isLocationAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_LOCATION);
    }

    /**
     * 判断WIFI功能是否可用
     *
     * @return true:可用，false：不可用
     */
    private boolean isWiFiAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_WIFI);
    }

}
