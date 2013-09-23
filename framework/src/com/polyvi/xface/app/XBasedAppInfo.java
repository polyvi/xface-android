
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


public class XBasedAppInfo {

    /** app id */
    private String mAppId;

    /** app名称 */
    private String mName;

    /** app版本号 */
    private String mVersion;

    /** 应用图标背景颜色 */
    private String mIconBackgroudColor;

    /** 应用图标路径 */
    private String mIcon;

    /** 应用的启动入口 */
    protected String mEntry;

    /**
     * 应用类型：
     */
    private String mType;

    public XBasedAppInfo() {
        mName = "";
        mVersion = "";
        mIcon = "";
        mType = "xapp";
    }

    /**
     * 设置应用id
     *
     * @param id
     *            应用id
     */
    public void setAppId(String id) {
        this.mAppId = id;
    }

    /**
     * 获取应用id
     */
    public String getAppId() {
        return mAppId;
    }

    /**
     * 获取应用名称
     */
    public String getName() {
        return mName;
    }

    /**
     * 设置应用名称
     *
     * @param mName
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * 获取应用版本号
     */
    public String getVersion() {
        return mVersion;
    }

    /**
     * 设置应用版本号
     *
     * @param mVersion
     */
    public void setVersion(String version) {
        this.mVersion = version;
    }

    /** 获取应用的类型 */
    public String getType() {
        return mType;
    }

    /** 设置应用的类型 */
    public void setType(String type) {
        this.mType = type;
    }

    /** 设置应用图标相对路径（相对于应用根目录） */
    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    /** 获取应用图标相对路径（相对于应用根目录） */
    public String getIcon() {
        return mIcon;
    }

    /** 设置应用图标背景颜色 */
    public void setIconBackgroudColor(String iconBackgroudColor) {
        this.mIconBackgroudColor = iconBackgroudColor;
    }

    /** 获取应用图标背景颜色 */
    public String getIconBackgroudColor() {
        return mIconBackgroudColor;
    }

    /** 设置应用的默认启动页面相对路径 */
    public void setEntry(String entry) {
        this.mEntry = entry;
    }

    /** 获取应用的默认启动页面相对路径，在没有设置的情况下返回index.html */
    public String getEntry() {
        return mEntry;
    }

}
