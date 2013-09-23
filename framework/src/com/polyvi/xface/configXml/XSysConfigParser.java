
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

package com.polyvi.xface.configXml;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.cordova.R;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.polyvi.xface.util.XLog;

public class XSysConfigParser {
    private static final String CLASS_NAME = XSysConfigParser.class.getSimpleName();
    private static final String TAG_PRE_INSTALL_PACKAGES = "pre_install_packages";
    private static final String TAG_APP_PACKAGE = "app_package";
    private static final String TAG_PREFERENCE = "preference";

    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_ID = "id";
    private static final String ATTR_LOG_LEVEL = "loglevel";
    private static final String ATTR_FULLSCREEN = "FullScreen";
    private static final String ATTR_WORK_DIR = "WorkDir";
    private static final String ATTR_ENGINE_VERSION = "EngineVersion";
    private static final String ATTR_ENGINE_BUILD = "EngineBuild";
    private static final String ATTR_SPLASH_DELAY = "SplashScreenDelayDuration";
    private static final String ATTR_AUTO_HIDE_SPLASH = "AutoHideSplashScreen";
    private static final String ATTR_UPDATE_ADDRESS = "UpdateAddress";
    private static final String ATTR_CHECK_UPDATE = "CheckUpdate";

    private XmlResourceParser mParser;

    public XSysConfigParser(Context context) {
        mParser = context.getResources().getXml(R.xml.config);
    }

    /**
     * 对config文件进行解析
     *
     * @return
     */
    public XSysConfigInfo parseConfig() throws XTagNotFoundException {
        if (null == mParser) {
            throw new XTagNotFoundException("error_format_config_xml");
        }
        XSysConfigInfo sysConfigInfo = new XSysConfigInfo();
        int eventType = -1;
        boolean insidePreInstallPackages = false;
        // 保存预装包的id和name
        ArrayList<XPreInstallPackageItem> preinstallPackages = new ArrayList<XPreInstallPackageItem>();
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            if (eventType == XmlResourceParser.START_TAG) {
                String strNode = mParser.getName();
                if (strNode.equals(TAG_PRE_INSTALL_PACKAGES)) {
                    insidePreInstallPackages = true;
                } else if (insidePreInstallPackages
                        && strNode.equals(TAG_APP_PACKAGE)) {
                    // 保存预安装包信息
                    String appId = mParser.getAttributeValue(null, ATTR_ID);
                    String packageName = mParser.getAttributeValue(null,
                            ATTR_NAME);
                    preinstallPackages.add(new XPreInstallPackageItem(
                            packageName, appId));
                    if (preinstallPackages.size() == 0) {
                        throw new XTagNotFoundException(TAG_APP_PACKAGE);
                    }
                } else if (strNode.equals(TAG_PREFERENCE)) {
                    String name = mParser.getAttributeValue(null, ATTR_NAME);
                    if (name.equals(ATTR_LOG_LEVEL)) {
                        // 设置log级别
                        String loglevel = mParser.getAttributeValue(null,
                                ATTR_VALUE);
                        sysConfigInfo.setLogLevel(loglevel);
                    } else if (name.equals(ATTR_FULLSCREEN)) {
                        // 设置是否全屏
                        String isFullScreen = mParser.getAttributeValue(null,
                                ATTR_VALUE);
                        sysConfigInfo.setFullscreen(isFullScreen);
                    } else if (name.equals(ATTR_SPLASH_DELAY)) {
                        // 设置splash的延迟时间
                        String splashDelay = mParser.getAttributeValue(null,
                                ATTR_VALUE);
                        sysConfigInfo.setSplashDelay(splashDelay);
                    } else if (name.equals(ATTR_AUTO_HIDE_SPLASH)) {
                        // 设置是否自动隐藏splash
                        String autoHideSplash = mParser.getAttributeValue(null,
                                ATTR_VALUE);
                        sysConfigInfo.setAutoHideSplash(autoHideSplash);
                    } else if (name.equals(ATTR_WORK_DIR)) {
                        // 设置工作目录设定策略
                        String workDir = mParser.getAttributeValue(null,
                                ATTR_VALUE);
                        sysConfigInfo.setWorkDir(workDir);
                    } else if (name.equals(ATTR_ENGINE_VERSION)) {
                        // 设置引擎版本号
                        String engineVersion = mParser.getAttributeValue(null,
                                ATTR_VALUE);
                        sysConfigInfo.setEngineVersion(engineVersion);
                    } else if (name.equals(ATTR_ENGINE_BUILD)) {
                        // 设置引擎构建号
                        String engineBuild = mParser.getAttributeValue(null,
                                ATTR_VALUE);
                        sysConfigInfo.setEngineBuild(engineBuild);
                    } else if (name.equals(ATTR_CHECK_UPDATE)) {
                        // 设置是否需要检测更新
                        String checkUpdate = mParser.getAttributeValue(null,
                                ATTR_VALUE);
                        sysConfigInfo.setCheckUpdate(checkUpdate);
                    } else if (name.equals(ATTR_UPDATE_ADDRESS)) {
                        // 设置检测更新的服务器地址
                        String updateAddress = mParser.getAttributeValue(null,
                                ATTR_VALUE);
                        sysConfigInfo.setUpdateAddress(updateAddress);
                    }
                }
            }
            try {
                eventType = mParser.next();
            } catch (XmlPullParserException e) {
                XLog.e(CLASS_NAME, "parseConfig: config.xml XmlPullParserException");
                throw new XTagNotFoundException("error_format_config_xml");
            } catch (IOException e) {
                XLog.e(CLASS_NAME, "parseConfig: config.xml IOException");
                throw new XTagNotFoundException("error_format_config_xml");
            }
        }
        sysConfigInfo.setPreinstallPackages(preinstallPackages);
        sysConfigInfo.setStartAppId(preinstallPackages.get(0).appId);
        return sysConfigInfo;
    }

}
