
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

package com.polyvi.xface.util;

/**
 * 封装jni native调用
 *
 */
public class XNativeBridge {
    static {
        System.loadLibrary("jniNativeBridge");
    }

    /**
     * 修改文件（夹）的权限
     * 请不要直接使用该api，而是使用{@link XFileUtils#setPermission}
     * @param path 文件（夹）所在路径
     * @param mode 文件（夹）的新权限值
     * （注：权限值是用八进制表示的）
     */
    public static native void chmod(String path, int mode);
}
