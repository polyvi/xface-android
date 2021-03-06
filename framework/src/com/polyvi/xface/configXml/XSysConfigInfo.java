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

import java.util.List;

/**
 * 负责记录解析config.xml得到的信息
 */
public class XSysConfigInfo {

    /** 所有要预装的应用安装包名 */
    private List<XPreInstallPackageItem> mPreinstallPackages;

    /** startApp id */
    private String mStartAppId;

    /** 系统LOG等级 */
    private String mLogLevel;

    /** 工作目录设定策略，1：仅手机内存;2：仅外部存储（FlashROM及SD/TF扩展卡）;3：外部存储优先 */
    private String mWorkDir;

    /** 是否自动隐藏splash图片 */
    private boolean mAutoHideSplash;

    /** 引擎的版本号 */
    private String mEngineVersion;

    /** 引擎的build号 */
    private String mEngineBuild;

    /** 加载应用时等待xface.js是否加载完成的时间 */
    private String mLoadUrlTimeout;

    private String mAppTransfer;

    public List<XPreInstallPackageItem> getPreinstallPackages() {
        return mPreinstallPackages;
    }

    public void setPreinstallPackages(
            List<XPreInstallPackageItem> mPreinstallPackages) {
        this.mPreinstallPackages = mPreinstallPackages;
    }

    public String getStartAppId() {
        return mStartAppId;
    }

    public void setStartAppId(String startAppId) {
        this.mStartAppId = startAppId;
    }

    public void setLogLevel(String logLevel) {
        this.mLogLevel = logLevel;
    }

    public String getLogLevel() {
        return mLogLevel;
    }

    public void setAutoHideSplash(String autoHideSplash) {
        if (null == autoHideSplash) {
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

    public void setLoadUrlTimeout(String loadUrlTimeout) {
        this.mLoadUrlTimeout = loadUrlTimeout;
    }

    public String getLoadUrlTimeout() {
        return mLoadUrlTimeout;
    }

    public String getAppTransfer(){
    	return mAppTransfer;
    }

    public void setAppTransfer(String transfer){
    	mAppTransfer = transfer;
    }

}
