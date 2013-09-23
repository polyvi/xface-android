
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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.cordova.PluginEntry;


/**
 * 负责记录解析config.xml得到的信息
 */
public class XSysConfigInfo {

    /** 所有要预装的应用安装包名 */
    private List<XPreInstallPackageItem> mPreinstallPackages;

    /** startApp id */
    private String mStartAppId;

    /** 系统允许执行的扩展列表 */
    private HashMap<String, PluginEntry> mSysExtensions;

    /**系统LOG等级 */
    private String mLogLevel;

    /**是否全屏 */
    private boolean mFullscreen;

    /**splash图片显示的时间 */
    private String mSplashDelay;

    /**工作目录设定策略，1：仅手机内存;2：仅外部存储（FlashROM及SD/TF扩展卡）;3：外部存储优先 */
    private String mWorkDir;

    /**是否显示splash图片 */
    private boolean mShowSplash;

    /**是否自动隐藏splash图片 */
    private boolean mAutoHideSplash;

    /**引擎的版本号 */
    private String mEngineVersion;

    /**引擎的build号 */
    private String mEngineBuild;

    /**检测更新的服务器地址 */
    private String mUpdateAddress;

    /**是否需要检测 */
    private boolean mUpdateCheck;

    /**加载应用时等待xface.js是否加载完成的时间 */
    private String mLoadUrlTimeout;

    /**从xml文件中加载的插件配置 */
    private HashMap<String, String> mPluginsConfig;

    /**从xml文件中加载的插件描述 */
    private Set<String> mPluginDesciptions;

    public List<XPreInstallPackageItem> getPreinstallPackages() {
        return mPreinstallPackages;
    }

    public void setPreinstallPackages(List<XPreInstallPackageItem> mPreinstallPackages) {
        this.mPreinstallPackages = mPreinstallPackages;
    }

    public String getStartAppId() {
        return mStartAppId;
    }

    public void setStartAppId(String startAppId) {
        this.mStartAppId = startAppId;
    }

    public HashMap<String, PluginEntry> getSysExtensions() {
        return mSysExtensions;
    }

    public void setSysExtensions(
            HashMap<String, PluginEntry> sysExtensions) {
        this.mSysExtensions = sysExtensions;
    }

    public void setLogLevel(String logLevel) {
        this.mLogLevel = logLevel;
    }

    public String getLogLevel() {
        return mLogLevel;
    }

    public void setFullscreen(String fullscreen) {
        if(null == fullscreen) {
            this.mFullscreen = false;
            return;
        }
        this.mFullscreen = fullscreen.equals("true");
    }

    public boolean getFullscreen() {
        return mFullscreen;
    }

    public void setSplashDelay(String splashDelay) {
        this.mSplashDelay = splashDelay;
    }

    public String getSplashDelay() {
        return mSplashDelay;
    }

    public void setShowSplash(String showSplash) {
        if(null == showSplash) {
            this.mShowSplash = false;
            return;
        }
        this.mShowSplash = showSplash.equals("true");
    }

    public boolean getShowSplash() {
        return mShowSplash;
    }

    public void setAutoHideSplash(String autoHideSplash) {
        if(null == autoHideSplash) {
            this.mAutoHideSplash = true;
            return;
        }
        this.mAutoHideSplash = autoHideSplash.equals("true");
    }

    public boolean getAutoHideSplash() {
        return mAutoHideSplash;
    }

    public void setWorkDir(String workDir) {
        this.mWorkDir = workDir;
    }

    public String getWorkDir() {
        return mWorkDir;
    }

    public void setEngineVersion(String engineVersion) {
        this.mEngineVersion = engineVersion;
    }

    public String getEngineVersion() {
        return mEngineVersion;
    }

    public void setEngineBuild(String engineBuild) {
        this.mEngineBuild = engineBuild;
    }

    public String getEngineBuild() {
        return this.mEngineBuild;
    }

    public void setUpdateAddress(String updateAddress) {
        this.mUpdateAddress = updateAddress;
    }

    public String getUpdateAddress() {
        return mUpdateAddress;
    }

    public void setUpdateCheck(String updateCheck) {
        if(null == updateCheck) {
            this.mUpdateCheck = false;
            return;
        }
        this.mUpdateCheck = updateCheck.equals("true");
    }

    public boolean getUpdateCheck() {
        return mUpdateCheck;
    }

    public void setLoadUrlTimeout(String loadUrlTimeout) {
        this.mLoadUrlTimeout = loadUrlTimeout;
    }

    public String getLoadUrlTimeout() {
        return mLoadUrlTimeout;
    }

    public void setPluginsConfig(HashMap<String, String> pluginsConfig) {
        mPluginsConfig = pluginsConfig;
    }

    public HashMap<String, String> getPluginsConfig() {
        return mPluginsConfig;
    }

    public void setPluginDesciptions(Set<String> pluginDesciptions) {
        mPluginDesciptions = pluginDesciptions;
    }

    public Set<String> getPluginDesciptions() {
        return mPluginDesciptions;
    }
}

