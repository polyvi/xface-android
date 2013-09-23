
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

/**
 * 应用安装进度监听器，负责安装进度及状态通知
 */
public interface XInstallListener {

    // FIXME: 后续需要修改状态的类别
    public enum InstallStatus {
        INSTALL_INITIALIZE, /** < 安装初始化 */
        INSTALL_UNZIP_PACKAGE, /** < 解包过程 */
        INSTALL_WRITE_CONFIGURATION, /** < 写配置的过程 */
        INSTALL_FINISHED,
        /** < 安装完成 */
    };

    /** 应用安装/卸载/更新操作错误码 */
    public enum AMS_ERROR {
        /** not used */
        ERROR_BASE,

        /** 应用安装包不存在 */
        NO_SRC_PACKAGE,

        /** 应用已经存在 */
        APP_ALREADY_EXISTED,

        /**IO 异常错误 */
        IO_ERROR,

        /** 没有找到待操作的目标应用 */
        NO_TARGET_APP,

        /** 不存在应用配置文件 */
        NO_APP_CONFIG_FILE,

        /** 保留字段, 兼容旧的REMOVE_APP_FAILED*/
        RESERVED,

        /** 未知错误 */
        UNKNOWN,
    };

    /** ams操作类型 */
    public enum AMS_OPERATION_TYPE {
        OPERATION_NONE,
        /** 安装操作类型 */
        OPERATION_TYPE_INSTALL,

        /** 卸载操作类型 */
        OPERATION_TYPE_UNINSTALL,

        /** 更新操作类型 */
        OPERATION_TYPE_UPDATE,
    };

    /**
     * 更新安装进度
     *
     * @param type
     *            类型标识：安装/卸载
     * @param progressState
     *            进度状态
     */
    public void onProgressUpdated(AMS_OPERATION_TYPE type,
            InstallStatus progressState);

    /**
     * 安装错误回调
     *
     * @param type
     *            类型标识：安装/卸载
     * @param appId
     * @param errorState
     *            错误码
     */
    public void onError(AMS_OPERATION_TYPE type, String appId,
            AMS_ERROR errorState);

    /**
     * 安装成功回调
     *
     * @param type
     *            类型标识：安装/卸载
     * @param appId
     *
     * @see XAppInstaller#OPERATION_TYPE_INSTALL
     * @see XAppInstaller#OPERATION_TYPE_UNINSTALL
     */
    public void onSuccess(AMS_OPERATION_TYPE type, String appId);
}
