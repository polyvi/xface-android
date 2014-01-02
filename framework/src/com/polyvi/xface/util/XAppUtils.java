
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.polyvi.xface.app.XAppInfo;
import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.configXml.XAbstractAppConfigParser;
import com.polyvi.xface.configXml.XXmlOperatorFactory;
import com.polyvi.xface.core.XConfiguration;

/**
 * 与app有关的工具类
 */
public class XAppUtils {
    private static final String CLASS_NAME = XAppUtils.class.getName();

    /**
     * 生成应用图标要放置的目标路径
     *
     * @param appId
     *            要拷贝的图标所在应用的id
     * @param relativeIconPath
     *            图标在应用内的相对路径
     * */
    public static String generateAppIconPath(String appId,
            String relativeIconPath) {
        if (null == relativeIconPath) {
            return null;
        }
        return new File(XConfiguration.getInstance().getWorkDirectory()
                + XApplication.APPS_ICON_DIR_NAME + File.separator + appId,
                relativeIconPath).getAbsolutePath();
    }

    /**
     * 从app安装包里面读取出app配置信息
     *
     * @param appPackagePath
     *            app安装包的路径
     * @return
     */
    public static XAppInfo getAppInfoFromAppPackage(String appPackagePath) {
        XAppInfo appInfo = null;
        File zipFile = new File(appPackagePath);
        InputStream is = null;
        try {
            is = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                } else if (XConstant.APP_CONFIG_FILE_NAME.equals(entry
                        .getName())) {
                    XAbstractAppConfigParser appConfigParser = XXmlOperatorFactory
                            .createAppConfigParser();
                    appConfigParser.setInput(zis);
                    if (null != appConfigParser) {
                        appInfo = appConfigParser.parseConfig();
                    }
                    break;
                }
            }
            zis.close();
            is.close();
        } catch (FileNotFoundException e) {
            XLog.e(CLASS_NAME, "The zip file: " + appPackagePath
                    + "does not exist!");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            XLog.e(CLASS_NAME, "IOException in reading zip file!");
            e.printStackTrace();
        }
        return appInfo;
    }

    /**
     * 解析app.xml文件，并返回app信息
     *
     * @param is
     *            app.xml的输入流
     * @return app信息
     */
    public static XAppInfo parseAppXml(InputStream is) {
        XAppInfo appInfo = null;
        XAbstractAppConfigParser appConfigParser = XXmlOperatorFactory
                .createAppConfigParser();
        appConfigParser.setInput(is);
        appInfo = appConfigParser.parseConfig();
        return appInfo;
    }

    /**
     * 初始化app的数据 从只读空间解压到安装目录
     *
     * @param ctx
     * @param appId
     * @return
     */
    public static boolean initAppData(Context ctx, String appId) {
        // 将内置数据解压到sdcard 速度较慢 不推荐内置太多数据
        String dataPackageName = XConstant.PRE_INSTALL_SOURCE_ROOT + appId
                + File.separator + XConstant.APP_WORK_DIR_NAME + File.separator
                + XConstant.APP_DATA_PACKAGE_NAME_IN_WORKSAPCE;
        String appRootInstalled = XConfiguration.getInstance()
                .getAppInstallDir() + appId + File.separator;
        if (XFileUtils.fileExists(ctx, XConstant.ASSERT_PROTACAL
                + dataPackageName)) {
            XFileUtils.unzipFileFromAsset(appRootInstalled
                    + XConstant.APP_WORK_DIR_NAME + File.separator, ctx,
                    dataPackageName);
            return true;
        }
        return false;
    }

    /**
     * 启动应用程序
     *
     * @param context
     * @param packageName
     *            应用程序包的名字
     * @param parameterName
     *            intent启动参数名称
     * @param parameterValue
     *            intent启动参数值
     * @return 成功返回true,失败返回false
     */
    public static boolean startNativeApp(Context context,
            String packageName, String parameterName, String parameterValue) {
        if (null == packageName) {
            return false;
        }

        PackageManager pm = context.getPackageManager();
        Intent intent = null;
        try {
            intent = pm.getLaunchIntentForPackage(packageName);
            if (null == intent) {
                return false;
            }
            intent.putExtra(parameterName, parameterValue);
            context.startActivity(intent);
        } catch (Exception e) {
            XLog.e(CLASS_NAME, "error when startNativeApp:" + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
