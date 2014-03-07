
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

package com.polyvi.xface.event;

public interface XEventType {

    // 短信消息
    public static final int MSG_RECEIVED = 4;
    // 电话消息
    public static final int CALL_RECEIVED = 5;

    // push消息
    public static final int PUSH_MSG_RECEIVED = 8;
    //关闭应用
    public static final int CLOSE_APP = 9;
    //关闭引擎
    public static final int CLOSE_ENGINE = 10;
    //app间通信事件
    public static final int XAPP_MESSAGE = 11;
    // 预装app转移完成事件
    public static final int TRANSFER_COMPLETE = 12;
    // 预装app的md5校验失败事件
    public static final int MD5_INVALID = 13;
    // 清除webview内存的缓存
    public static final int CLEAR_MEMORY_CACHE = 14;
    //收到外部第三方程序发送过来参数的消息
    public static final int EXTERNAL_MESSAGE_RECEIVED= 15;
    //用户自定义事件类型
    public static final int USER_CUSTOM_EVENT = 10000;
}
