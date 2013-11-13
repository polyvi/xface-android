
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

public interface XAMSError {

    /** 应用安装/卸载/更新/启动操作错误码 */
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

        /** 应用不存在 */
        APP_NOT_FOUND,

        /** 应用已经启动 */
        APP_ALREADY_RUNNING,

        /** 应用入口错误 */
        APP_ENTRY_ERR,

        /** 启动native应用错误 */
        START_NATIVE_APP_ERR,

        /** 未知错误 */
        UNKNOWN,
    };

}
