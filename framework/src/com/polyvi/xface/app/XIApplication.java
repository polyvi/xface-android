
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

import com.polyvi.xface.XStartParams;


/**
 * 负责程序的启动,关闭等
 */
public interface XIApplication {

    /**
     * 启动程序
     *
     * @param appid
     *            程序的id号码
     * @param params
     *            启动程序参数
     * @return true:启动成功,false:启动失败
     */
    boolean start(XStartParams params);

    /**
     * 关闭程序
     *
     * @param appid
     *            程序的id号码
     * @return true：成功,false:失败
     */
    boolean close();

    /**
     * 获取程序的id号码
     *
     * @return 程序的id号
     */
    String getAppId();

    /**
     * 获取应用描述信息
     *
     * @return
     */
    public XAppInfo getAppInfo();

    /**
     * 获取app的图片url
     * @return url
     */
    public String getAppIconUrl();

    /**
     * 更新应用程序信息
     * @param appInfo
     */
    public void updateAppInfo(XAppInfo appInfo);

}
