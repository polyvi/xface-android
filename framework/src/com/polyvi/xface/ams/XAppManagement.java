
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
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.Stack;

import com.polyvi.xface.XStartParams;
import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.app.XApplicationCreator;
import com.polyvi.xface.app.XIApplication;
import com.polyvi.xface.app.XNativeApplication;
import com.polyvi.xface.core.XISystemContext;
import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XStringUtils;
import com.polyvi.xface.view.XAppWebView;

/**
 * 负责应用的安装、卸载、运行和更新
 */
public class XAppManagement {

    private static final String CLASS_NAME = XAppManagement.class
            .getSimpleName();

    /** app安装器 */
    private XAppInstaller mAppInstaller;

    /** 该栈用于管理已添加的app */
    private Stack<XIApplication> mAppStack;

    /** 本地应用installer的托管 */
    private XNativeAppInstallDelegate mNativeAppInstallDelegate;

    private XApplicationCreator mAppCreator;

    /** 空的安装器事件监听器，在不需要监听安装/更新/卸载事件时使用 */
    private static final XInstallListener EMPTRY_INSTALL_LISTENER = new XInstallListener() {
        @Override
        public void onProgressUpdated(AMS_OPERATION_TYPE type,
                InstallStatus progressState) {
        }

        @Override
        public void onError(AMS_OPERATION_TYPE type, String appId,
                AMS_ERROR errorState) {
        }

        @Override
        public void onSuccess(AMS_OPERATION_TYPE type, String appId) {
        }
    };

    public XAppManagement(XApplicationCreator appCreator) {
        this.mAppCreator = appCreator;
    }

    /**
     * ams模块的初始化工作
     */
    public void init(XISystemContext systemContext) {

        initAppInstaller(systemContext);
        // 其他的初始化放在这里
        mAppStack = new Stack<XIApplication>();
    }

    /**
     * 初始化AppInstaller
     */
    private void initAppInstaller(XISystemContext context) {
        if (null == this.mAppInstaller) {
            this.mAppInstaller = new XAppInstaller(context,
                    mAppCreator);
            mNativeAppInstallDelegate = new XNativeAppInstallDelegate(context);
        }
    }

    /**
     * 初始化AppList， 将系统已经安装的app初始化到appList中
     */
    public final XAppList getAppList() {
        return mAppInstaller.getInstalledAppList();
    }

    /**
     * 启动一个app
     *
     * @param appId
     *            应用id
     *
     * @return 启动应用是否成功
     */
    public boolean startApp(String appId, XStartParams params) {
        XIApplication app = getAppList().getAppById(appId);
        if (null == app) {
            XLog.w(CLASS_NAME, "Start app failed, can't find app by id: "
                    + appId);
            return false;
        }
        String startData = null;
        if (null != params) {
            startData = params.data;
        }
        if (isNativeApp(app)) {
            String entry = app.getAppInfo().getEntry();
            if (XStringUtils.isEmptyString(entry)) {
                return false;
            }
            if (!mNativeAppInstallDelegate.isAppInstalled(entry)) {
                String apkPath = XNativeAppInstallDelegate
                        .getApkPathFromInstallDir(appId);
                if (XStringUtils.isEmptyString(apkPath)
                        || !new File(apkPath).exists())
                    return false;
                mNativeAppInstallDelegate.installApp(apkPath,
                        EMPTRY_INSTALL_LISTENER);
                return true;
            }
            return app.start(params);
        } else {
            // 如果app处于活动状态即已经被启动，不能再次被启动
            XApplication webApp = XApplicationCreator.toWebApp(app);
            if (webApp.isActive()) {
                return false;
            }
            mAppStack.push(webApp);
            String pageEntry = null;
            if (null != params) {
                pageEntry = params.pageEntry;
            }
            if (!XStringUtils.isEmptyString(pageEntry)) {
                webApp.getAppInfo().setEntry(pageEntry);
            }
            if (!XStringUtils.isEmptyString(startData)) {
                webApp.setData(XConstant.TAG_APP_START_PARAMS, startData);
            }
            webApp.start(params);
            return true;
        }

    }

    /**
     * 根据app类型得到相应的安装listener
     *
     * @param path
     *            app的路径
     * @return 监听
     */
    private XInstallListener wrapInstallerListener(XInstallListener listener,
            String path) {
        if (isNativePackage(path)) {
            XNativeAppInstallerListener nativeListener = new XNativeAppInstallerListener(
                    listener, mNativeAppInstallDelegate);
            return nativeListener;
        } else {
            return listener;
        }
    }

    /**
     * 根据app的类型得到相应的卸载listener
     *
     * @param id
     *            app的id
     * @return 监听
     */
    private XInstallListener wrapUninstallerListener(XInstallListener listener,
            String id) {
        if (isNativeApp(id)) {
            XNativeAppInstallerListener nativeListener = new XNativeAppInstallerListener(
                    listener, mNativeAppInstallDelegate, getAppList()
                            .getAppById(id));
            return nativeListener;
        }
        return listener;
    }

    /**
     * 安装一个应用
     *
     * @param path
     *            应用安装包的绝对路径
     * @param listener
     *            安装监听器
     */
    public String installApp(String path, XInstallListener listener) {
        return mAppInstaller.install(path,
                wrapInstallerListener(listener, path));
    }

    /**
     * 安装一个应用
     *
     * @param path
     *            应用安装包的绝对路径
     * @param appId
     *            应用的appId
     * @param listener
     *            安装监听器
     */
    public String installApp(String path, String appId,
            XInstallListener listener) {
        return mAppInstaller.install(path, appId, listener);
    }

    /**
     * 更新一个应用
     *
     * @param path
     *            安装包路径
     * @param listener
     *            更新监听器
     */
    public void updateApp(String path, XInstallListener listener) {
        mAppInstaller.update(path, wrapInstallerListener(listener, path));
    }

    /**
     * 卸载应用
     *
     * @param appId
     * @param listener
     *            卸载监听器
     */
    public void uninstallApp(String appId, XInstallListener listener) {
        mAppInstaller
                .uninstall(appId, wrapUninstallerListener(listener, appId));
    }

    /**
     * 安装一个应用
     *
     * @param path
     *            应用安装包的绝对路径
     */
    public String installApp(String path) {
        return installApp(path,
                wrapInstallerListener(EMPTRY_INSTALL_LISTENER, path));
    }

    public String installApp(String path, String appid) {
        if (XStringUtils.isEmptyString(appid)) {
            return installApp(path);
        }
        return installApp(path, appid,
                wrapInstallerListener(EMPTRY_INSTALL_LISTENER, path));
    }

    /**
     * 更新一个应用
     *
     * @param path
     *            安装包路径
     */
    public void updateApp(String path) {
        updateApp(path, wrapInstallerListener(EMPTRY_INSTALL_LISTENER, path));
    }

    /**
     * 关闭应用
     *
     * @param viewId
     *            需要关闭应用对应的viewId
     */
    public void closeApp(int viewId) {
        XAppList appList = getAppList();
        String appId = appList.getAppIdByViewId(viewId);
        assert appId != null;
        closeApp(appId);
    }

    /**
     * 关闭应用
     *
     * @param appId
     */
    public void closeApp(String appId) {
        XApplication app = XApplicationCreator.toWebApp(getAppList()
                .getAppById(appId));
        if (null == app) {
            XLog.e(CLASS_NAME, "Close app failed, can't find app by id: "
                    + appId);
            return;
        }
        if (mAppStack.empty()) {
            XLog.d(CLASS_NAME, "The app stack is empty when closing app!");
            return;
        }
        mAppStack.remove(app);
        app.close();
    }

    /**
     * 得到当前活动的app
     * */
    public XIApplication getCurrActiveApp() {
        if (mAppStack.empty()) {
            XLog.d(CLASS_NAME,
                    "The app stack is empty when getting current Active App!");
            return null;
        }
        return mAppStack.peek();
    }

    /**
     * 处理app的通讯消息
     *
     * @param view
     * @param data
     */
    public void handleAppMessage(XApplication startApp, XAppWebView view,
            String msgData) {
        XAppList list = getAppList();
        XApplication app = list.getAppByViewId(view.getViewId());
        String jsScript = "try{cordova.require('com.polyvi.xface.extension.ams.app').fireAppEvent('message',"
                + msgData
                + ");}catch(e){console.log('exception in fireAppEvent:' + e);}";
        if (null != app) {
            startApp.loadJavascript(jsScript);
        } else {
            Iterator<XIApplication> apps = mAppStack.iterator();
            while (apps.hasNext()) {
                XApplication xapp = XApplicationCreator.toWebApp(apps.next());
                if (null == xapp) {
                    continue;
                }
                    xapp.loadJavascript(jsScript);
            }
        }
    }

    /**
     * 获取预置包
     *
     * @return 返回预置包数组，每一项是预置包名
     * */
    public String[] getPresetAppPackages(String startAppWorkSpace) {
        assert null != startAppWorkSpace;
        File presetPackageDir = new File(startAppWorkSpace,
                XConstant.PRE_SET_APP_PACKAGE_DIR_NAME);
        if (!presetPackageDir.exists()) {
            return null;
        }
        return presetPackageDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                File presetFile = new File(file.getAbsolutePath(), name);
                if (presetFile.isFile()) {
                    return name.endsWith(XConstant.APP_PACKAGE_SUFFIX)
                            || name.endsWith(XConstant.APP_PACKAGE_SUFFIX_XPA)
                            || name.endsWith(XConstant.APP_PACKAGE_SUFFIX_XSPA);
                }
                return false;
            }
        });
    }

    /**
     * 在xFace退出的时候要关闭所有app
     * */
    public void closeAllApp() {
        while (null != mAppStack && !mAppStack.empty()) {
            XIApplication app = mAppStack.peek();
            closeApp(app.getAppId());
        }
    }

    /**
     * 判断是否是native app的类型
     *
     * @param app
     *            被判断的app类型
     * @return 是native类型,不是native类型
     */
    boolean isNativeApp(XIApplication app) {
        return app instanceof XNativeApplication;
    }

    /**
     * 判断是否是native app的类型
     *
     * @param app
     *            被判断的app的id
     * @return true：是native类型,false:不是native类型
     */
    boolean isNativeApp(String appId) {
        XIApplication app = getAppList().getAppById(appId);
        return isNativeApp(app);
    }

    /**
     * 判断压缩包是否为NativeApp的压缩包
     *
     * @param path
     *            压缩包的路径
     * @return true:是native压缩包,false不是native压缩包
     */
    boolean isNativePackage(String path) {
        return path.endsWith(XConstant.NATIVE_APP_SUFFIX_NPA);
    }

    /**
     * 获得app的创建工厂
     */
    public XApplicationCreator getAppCreator() {
        return mAppCreator;
    }
}
