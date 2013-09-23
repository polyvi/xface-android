
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

package com.polyvi.xface.extension;

import org.apache.cordova.CordovaWebView;

import com.polyvi.xface.ams.XAppList;
import com.polyvi.xface.ams.XInstallListener;

/**
 * 封装ams的各种操作，主要包括app的基本操作，设置监听器等功能，以提供给extension使用
 */
public interface XAms {

    /**
     * 启动一个应用
     * 
     * @param appId
     *            应用的id
     * @param params
     *            启动程序参数
     * @return ture:启动成功,false:启动失败
     */
    public abstract boolean startApp(String appId, String params);

    /**
     * 安装一个应用
     * 
     * @param webContext
     *            调用该接口的应用对象
     * @param path
     *            应用安装包的相对路径（路径开头的/表示应用的工作目录）
     * 
     * @param listner
     *            安装监听器
     */
    public abstract void installApp(CordovaWebView webContext, String path,
            XInstallListener listner);

    /**
     * 更新一个应用
     * 
     * @param webContext
     *            调用该接口的应用对象
     * @param path
     *            安装包相对路径（路径开头的/表示应用的工作目录）
     * @param listener
     *            更新监听器
     */
    public abstract void updateApp(CordovaWebView webContext, String path,
            XInstallListener listener);

    /**
     * 卸载应用
     * 
     * @param appId
     * @param listener
     *            卸载监听器
     */
    public abstract void uninstallApp(String appId, XInstallListener listener);

    /**
     * 关闭应用
     * 
     * @param appId
     */
    public abstract void closeApp(String appId);

    /**
     * 获取应用列表
     * 
     * @return
     */
    public abstract XAppList getAppList();

    /**
     * 获取预置包
     * 
     * @param startAppWorkSpace startApp的工作空间
     * @return 返回预置包数组，每一项是预置包名
     * */
    public abstract String[] getPresetAppPackages(String startAppWorkSpace);

}
