
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

import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XStringUtils;

/**
 * 应用配置信息封装类
 */
public class XAppInfo extends XBasedAppInfo {

    // TODO: 宽高以后要考虑到分辨率适配的问题
    /** app显示的宽度 */
    private int mWidth;

    /** app显示的高度 */
    private int mHeight;

    /** 是否加密 */
    private boolean mIsEncrypted;

    /** 应用的运行模式：本地模式，在线模式 */
    private String mAppRunningMode;

    /** 定义此应用基于1.x or 3.x引擎运行，后面研究混合模式需要用到 */
    private String mEngineType;
    /** 渠道ID */
    private String mChannelId;
    /** 渠道名称 */
    private String mChannelName;

    /**app源码的root*/
    private String mSourceRoot;

    public XAppInfo() {
        mAppRunningMode = "local";
        mChannelId = "";
        mChannelName = "";
    }

    /** 获取应用宽度 */
    public int getWidth() {
        return mWidth;
    }

    /** 设置应用的宽度 */
    public void setWidth(int width) {
        this.mWidth = width;
    }

    /** 获取应用的高度 */
    public int getHeight() {
        return mHeight;
    }

    /** 设置应用的高度 */
    public void setHeight(int height) {
        this.mHeight = height;
    }

    /** 应用是否加密 */
    public boolean isEncrypted() {
        return mIsEncrypted;
    }

    /** 设置应用是否加密 */
    public void setEncrypted(boolean isEncrypted) {
        this.mIsEncrypted = isEncrypted;
    }

    /** 设置应用运行模式 */
    public void setRunModeConfig(String runningMode) {
        this.mAppRunningMode = runningMode;
    }

    /** 获取应用运行模式 */
    public String getRunModeConfig() {
        return this.mAppRunningMode;
    }

    /**
     * 获得应用是基于1.x还是3.x引擎运行
     */
    public String getEngineType() {
        return mEngineType;
    }

    /**
     * 设置应用基于1.x还是3.x引擎运行
     */
    public void setEngineType(String engineType) {
        mEngineType = engineType;
    }

    /**
     * 获取渠道id
     */
    public String getChannelId() {
        return mChannelId;
    }

    /**
     * 设置渠道id
     */
    public void setChannelId(String channelId) {
        mChannelId = channelId;
    }

    /**
     * 获取渠道名称
     */
    public String getChannelName() {
        return mChannelName;
    }

    /**
     * 设置渠道名称
     */
    public void setChannelName(String channelName) {
        mChannelName = channelName;
    }

    @Override
    public String getEntry() {
        return XStringUtils.isEmptyString(mEntry) ? XConstant.DEFAULT_START_PAGE_NAME
                : mEntry;
    }

    /**
     * 设置源码的root
     *
     * @param root
     */
    public void setSrcRoot(String root) {
        mSourceRoot = root;
    }

    /**
     * 得到源码root
     *
     * @return
     */
    public String getSrcRoot() {
        return mSourceRoot;
    }
}
