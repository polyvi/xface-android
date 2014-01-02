
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

package com.polyvi.xface.app;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.webkit.WebSettings;

import com.polyvi.xface.XSecurityPolicy;
import com.polyvi.xface.XStartParams;
import com.polyvi.xface.core.XAppRunningMode;
import com.polyvi.xface.core.XConfiguration;
import com.polyvi.xface.core.XIResourceFilter;
import com.polyvi.xface.core.XISystemContext;
import com.polyvi.xface.core.XIdleWatcher;
import com.polyvi.xface.core.XLocalMode;
import com.polyvi.xface.event.XEvent;
import com.polyvi.xface.event.XEventType;
import com.polyvi.xface.event.XSystemEventCenter;
import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XFileUtils;
import com.polyvi.xface.util.XStringUtils;
import com.polyvi.xface.view.XAppWebView;

/**
 * 用于描述一个app应用，包含app的描述信息、状态及UI界面
 */
public class XApplication implements XIApplication {

    /** 所有应用图标所在目录的名称 */
    public static final String APPS_ICON_DIR_NAME = "app_icons";

    public static final String TAG_EXT_PERMISSIONS = "all";

    /** 系统上下文环境 */
    private XISystemContext mSysContext;

    /** app对应的视图 */
    private XAppWebView mAppView;

    /** 应用描述信息 */
    private XAppInfo mAppInfo;

    /** app的workspace */
    private String mWorkSpace = "";

    /** 用于存放App的通信数据 */
    private Map<String, Object> mDatas;

    private boolean mIsOverrideBackbutton = false;
    private boolean mIsOverrideVolumeButtonDown = false;
    private boolean mIsOverrideVolumeButtonUp = false;

    /** < 默认为本地运行模式 */
    private XAppRunningMode mRunningMode = new XLocalMode();

    /** 监视app是否处于空闲状态 */
    private XIdleWatcher mWatcher;

    /** app的安全策略 */
    private XSecurityPolicy mSecurityPolicy;

    public XApplication(XAppInfo appInfo) {
        updateAppInfo(appInfo);
    }

    public XApplication(String appId) {
        mAppInfo = new XAppInfo();
        mAppInfo.setAppId(appId);
    }

    public void init(XISystemContext sysContext) {
        mSysContext = sysContext;
        mDatas = new Hashtable<String, Object>();
        initWorkSpace();

    }

    /**
     * 获取app对应的view
     */
    public XAppWebView getView() {
        return mAppView;
    }

    /**
     * 获取应用描述信息
     *
     * @return
     */
    public XAppInfo getAppInfo() {
        return mAppInfo;
    }

    /**
     * 设置应用配置信息
     */
    public void updateAppInfo(XAppInfo appInfo) {
        this.mAppInfo = appInfo;
        initAppRunningMode();
    }

    /**
     * 获取应用id
     */
    public String getAppId() {
        return mAppInfo.getAppId();
    }

    /**
     * 获取应用视图id
     *
     * @return 应用id
     */
    public int getViewId() {
        return mAppView == null ? XAppWebView.EMPTPY_VIEW_ID : mAppView
                .getViewId();
    }

    /**
     * 将app加载到appView上面显示
     *
     * @param url
     *            [in] 应用的url
     */
    public void loadAppIntoView(String url) {
        mSysContext.loadView(this, url);
    }

    public void loadAppIntoView(String url, boolean showWaiting) {
        mSysContext.loadView(this, url);
    }

    /**
     * 获取app的图片url
     *
     * @return url
     */
    public String getAppIconUrl() {
        return mRunningMode.getIconUrl(mAppInfo);
    }

    /**
     * 设置app运行模式
     *
     * @param mode
     *            [in] app运行模式
     */
    public void setAppRunningMode(XAppRunningMode mode) {
        if (mode != null)
            mRunningMode = mode;
    }

    /**
     * 初始化app的运行模式 根据app的配置文件指定运行模式
     */
    private void initAppRunningMode() {
        setAppRunningMode(XAppRunningMode.createAppRunningMode(mAppInfo
                .getRunModeConfig()));
    }

    /**
     * 初始化应用程序的工作目录，若不存在，创建该目录，然后设置工作目录为其他用户可读
     */
    private void initWorkSpace() {
        mWorkSpace = XConfiguration.getInstance().getAppInstallDir()
                + getAppId() + File.separator + XConstant.APP_WORK_DIR_NAME;
        File appWorkDir = new File(mWorkSpace);
        if (!appWorkDir.exists()) {
            appWorkDir.mkdirs();
        }
        // 设置工作目录的权限为其它用户可执行
        setWorkSpaceExecutableByOther();
    }

    /**
     * 返回该应用程序的工作目录
     *
     * @return 应用程序的工作目录
     */
    public String getWorkSpace() {
        return mWorkSpace;
    }

    /**
     * 设置工作目录的权限为其它用户可执行
     */
    private void setWorkSpaceExecutableByOther() {
        XFileUtils.setPermission(XFileUtils.EXECUTABLE_BY_OTHER, XConfiguration
                .getInstance().getAppInstallDir());
        XFileUtils.setPermission(XFileUtils.EXECUTABLE_BY_OTHER, XConfiguration
                .getInstance().getAppInstallDir() + getAppId());
        XFileUtils.setPermission(XFileUtils.EXECUTABLE_BY_OTHER, mWorkSpace);
    }

    /**
     * 返回存放该应用程序数据的目录，若不存在，创建该目录 应用对该目录没有读写权限
     *
     * @return 存放该应用程序数据的目录
     */
    public String getDataDir() {
        String dataDirPath = XConfiguration.getInstance().getAppInstallDir()
                + getAppId() + File.separator + XConstant.APP_DATA_DIR_NAME;
        File appDataDir = new File(dataDirPath);
        if (!appDataDir.exists()) {
            appDataDir.mkdirs();
        }
        return dataDirPath;
    }

    /**
     * 获取资源迭代器
     *
     * @param filter
     *            安全资源过滤器
     * @return
     */
    public Iterator<byte[]> getResourceIterator(XIResourceFilter filter) {
        return mRunningMode.createResourceIterator(this, filter);
    }

    /**
     * 当前app是否是活动状态的
     */
    public boolean isActive() {
        return null != mAppView;
    }

    /**
     * 设置backbutton是否被重写
     *
     * @param overrideBackbutton
     *            为true表示要重写 为false表示不重写
     */
    public void setOverrideBackbutton(boolean overrideBackbutton) {
        mIsOverrideBackbutton = overrideBackbutton;
    }

    /**
     * 设置volume button down是否被重写
     *
     * @param overrideVolumeButtonDown
     *            为true表示要重写 为false表示不重写
     */
    public void setOverrideVolumeButtonDown(boolean overrideVolumeButtonDown) {
        mIsOverrideVolumeButtonDown = overrideVolumeButtonDown;
    }

    /**
     * 设置volume button up是否被重写
     *
     * @param overrideVolumeButtonUp
     *            为true表示要重写 为false表示不重写
     */
    public void setOverrideVolumeButtonUp(boolean overrideVolumeButtonUp) {
        mIsOverrideVolumeButtonUp = overrideVolumeButtonUp;
    }

    /**
     * 得到backbutton是否被重写
     *
     * @return 返回true表示被重写 返回false表示没有重写
     */
    public boolean isOverrideBackbutton() {
        return mIsOverrideBackbutton;
    }

    /**
     * 得到volume button down是否被重写
     *
     * @return 返回true表示被重写 返回false表示没有重写
     */
    public boolean isOverrideVolumeButtonDown() {
        return mIsOverrideVolumeButtonDown;
    }

    /**
     * 得到volume button up是否被重写
     *
     * @return 返回true表示被重写 返回false表示没有重写
     */
    public boolean isOverrideVolumeButtonUp() {
        return mIsOverrideVolumeButtonUp;
    }

    /**
     * 存放app的数据的数据
     *
     * @param key
     * @param value
     */
    public void setData(String key, Object value) {
        mDatas.put(key, value);
    }

    /**
     * 删除数据
     *
     * @param key
     */
    public void removeData(String key) {
        mDatas.remove(key);
    }

    /**
     * 获得数据
     *
     * @param key
     *            键值
     * @return
     */
    public Object getData(String key) {
        return mDatas.get(key);
    }

    public String getIntalledDir() {
        return XConfiguration.getInstance().getAppInstallDir()
                + mAppInfo.getAppId() + File.separator;
    }

    /**
     * 清理页面缓存.
     * @param includeDiskFile 是否包含磁盘文件
     */
    public void clearCache(boolean includeDiskFile) {
        mAppView.clearCache(includeDiskFile);
    }

    /**
     * 卸载应用的缓存数据,例如：离线应用的缓存、http缓存、localStorage信息
     *
     * @param context
     */
    public void releaseData(Context context) {
        //发送清除webview的缓存的事件
        XEvent evt = new XEvent(XEventType.CLEAR_MEMORY_CACHE);
        XSystemEventCenter.getInstance().sendEventSync(evt);
        mRunningMode.clearAppData(this, context);
    }

    /**
     * 加载错误显示页面
     */
    public void loadErrorPage() {
        String errorPageUrl = XConstant.FILE_SCHEME + getDataDir()
                + File.separator + XConstant.ERROR_PAGE_NAME;
        loadAppIntoView(errorPageUrl);
    }

    public void setView(XAppWebView view) {
        mAppView = view;
        view.setOwnerApp(this);
    }

    @Override
    public boolean start(XStartParams params) {
        // 处理启动参数 和页面
        String pageEntry = null;
        String startData = null;
        if (null != params) {
            pageEntry = params.pageEntry;
            startData = params.data;
        }
        if (!XStringUtils.isEmptyString(pageEntry)) {
            mAppInfo.setEntry(pageEntry);
        }
        if (!XStringUtils.isEmptyString(startData)) {
            setData(XConstant.TAG_APP_START_PARAMS, startData);
        }
        if(null == mSecurityPolicy) {
            //安全策略为空，则采用系统默认的安全策略
            mSecurityPolicy = mSysContext.getSecurityPolicy();
        }
        mRunningMode.loadApp(this, mSecurityPolicy);
        return true;
    }

    @Override
    public boolean close() {
        if (null != mWatcher) {
            mWatcher.stop();
        }
        closeView();
        return mSysContext.getSecurityPolicy().checkAppClose(this);
    }

    public String getBaseUrl() {
        return mRunningMode.getAppUrl(this);
    }

    /**
     * 尝试显示app视图
     */
    public void tryShowView() {
        // mSysContext.waitingDialogForAppStartFinished();
        // if (!mSysContext.isSplashShowing()) {
        // showView();
        // }
    }

    /**
     * 启动监视器
     */
    public void startIdleWatcher(long interval, Runnable task) {
        if (null != mWatcher) {
            stopIdleWatcher();
        }
        mWatcher = new XIdleWatcher();
        mWatcher.start(interval, task);
    }

    /**
     * 停止监视
     */
    public void stopIdleWatcher() {
        if (mWatcher != null) {
            mWatcher.stop();
            mWatcher = null;
        }
    }

    /**
     * 重置IdleWatcher
     */
    public void resetIdleWatcher() {
        if (null != mWatcher) {
            mWatcher.notifyOperatered();
        }
    }

    /**
     * 关闭app view
     */
    private void closeView() {
        mAppView.willClosed();
        mSysContext.unloadView(mAppView);
        if (null != mAppView) {
            this.mAppView.setValid(false);
        }
        mAppView = null;
    }

    /**
     * 设置缓存策略 不同的运行模式 有不同的策略
     *
     * @param settings
     */
    public void setCachePolicy(WebSettings settings) {
        mRunningMode.setAppCachedPolicy(settings);
    }

    /**
     * 获取系统上下文环境
     *
     * @return
     */
    public XISystemContext getSystemContext() {
        return mSysContext;
    }

    /**
     * 加载js
     *
     * @param statement
     *            js参数
     */
    public void loadJavascript(String statement) {
        StringBuffer sb = new StringBuffer();
        sb.append("javascript:");
        sb.append(statement);
        this.getView().loadUrl(sb.toString());
    }

    /**
     * 设置安全策略
     * @param policy
     */
    public void setAppSecurityPolicy(XSecurityPolicy policy) {
        mSecurityPolicy = policy;
    }

}
