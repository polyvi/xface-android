
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

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import com.polyvi.xface.ams.XInstallListener.AMS_ERROR;
import com.polyvi.xface.ams.XInstallListener.AMS_OPERATION_TYPE;
import com.polyvi.xface.core.XConfiguration;
import com.polyvi.xface.core.XISystemContext;
import com.polyvi.xface.util.XFileUtils;
import com.polyvi.xface.util.XStringUtils;

public class XNativeAppInstallDelegate {

    private final static String FILE_HEAD = "file:";

    private static final String APK_TYPE = ".apk";

    private final static String INSTALL_ARCHIVE = "application/vnd.android.package-archive";

    private static final String APPID_PACKAGE = "package:";

    private Context mContext;

    /** 存放listener的map */
    private HashMap<String, XInstallListener> listenerMap;

    public XNativeAppInstallDelegate(XISystemContext ctx) {
        mContext = ctx.getContext();
        listenerMap = new HashMap<String, XInstallListener>();
    }

    /**
     * 安装本地程序
     *
     * @param filePath
     *            nativeapp的存放路径
     * @param listener
     *            监听
     */
    public void installApp(final String filePath,
            final XInstallListener listener) {
        if(XStringUtils.isEmptyString(filePath))
        {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            listener.onError(AMS_OPERATION_TYPE.OPERATION_TYPE_INSTALL,
                    filePath, AMS_ERROR.NO_SRC_PACKAGE);
            return;
        }
        XFileUtils.setPermission(XFileUtils.READABLE_AND_EXECUTEBLE_BY_OTHER, file.getAbsolutePath());
        final String packageName = getPackageName(filePath);
        listenerMap.put(packageName, listener);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse(FILE_HEAD + filePath), INSTALL_ARCHIVE);
        mContext.startActivity(intent);
    }

    /**
     * 卸载本地程序
     *
     * @param appId
     *            程序的id
     * @param listener
     *            监听
     */
    public void uninstallApp(final String appId, final XInstallListener listener) {
        if (null == appId) {
            return;
        }
        if (!isAppInstalled(appId)) {
            listener.onError(AMS_OPERATION_TYPE.OPERATION_TYPE_UNINSTALL,
                    appId, AMS_ERROR.NO_SRC_PACKAGE);
            return;
        }
        listenerMap.put(appId, listener);
        Intent uninstallIntent = null;
        if (appId.startsWith(APPID_PACKAGE)) {
            uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.parse(appId));
        } else {
            uninstallIntent = new Intent(Intent.ACTION_DELETE,
                    Uri.parse(APPID_PACKAGE + appId));
        }
        uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(uninstallIntent);
    }

    /**
     * 更新程序
     *
     * @param packagePath
     *            更新的native程序的存放路径
     * @param listener
     *            监听
     */
    public void updateApp(String packagePath, XInstallListener listener) {
        installApp(packagePath,listener);
    }

    /**
     * 判断程序是否安装成功
     *
     * @param appId
     *            程序id号码
     * @return true：系统已经安装，程序未安装
     */
    public boolean isAppInstalled(String appId) {
        if (null == appId) {
            return false;
        }

        try {
            mContext.getPackageManager().getApplicationInfo(appId,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 从nativeapp的路径获取其包名
     *
     * @param path
     *            nativeapp的存放路径
     * @return 包名
     */
    private String getPackageName(String path) {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            return appInfo.packageName;
        }
        return null;
    }

    /**
     * 遍历app安装目录得到native app安装包apk文件的路径
     *
     * @param appId
     *            app的id
     * @return apk的路径
     */
    public static String getApkPathFromInstallDir(String appId)
    {
        if(XStringUtils.isEmptyString(appId))
        {
            return null;
        }
        String appDirPath = XConfiguration.getInstance().getAppInstallDir()
                + appId;
        File[] files = new File(appDirPath).listFiles();
        for (int i = 0; i < files.length; i++) {
            String path = files[i].getAbsolutePath();
            if (path.endsWith(APK_TYPE)) {
                return path;
            }
        }
        return null;
    }
}