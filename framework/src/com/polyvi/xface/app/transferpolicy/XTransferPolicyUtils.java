
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

package com.polyvi.xface.app.transferpolicy;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.polyvi.xface.util.XCryptor;
import com.polyvi.xface.util.XLog;

/**
 * 转移策略的工具类
 */
public class XTransferPolicyUtils {
    private static final String CLASS_NAME = XTransferPolicyUtils.class
            .getSimpleName();

    /**
     * 排序md5数组并求出总的md5值
     *
     * @param md5Array
     *            md5数组列表
     * @param cryptor
     * @return
     */
    public static String calAppMd5(ArrayList<String> md5Array, XCryptor cryptor) {
        try {
            if (null == md5Array) {
                XLog.e(CLASS_NAME, "Md5 Array is null!");
                return null;
            }
            // 排序
            Comparator<String> comparator = new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            };
            Collections.sort(md5Array, comparator);
            // 对迭代器中所有元素的md5值再求一次md5值
            byte[] appFileMD5Buffer = null;
            appFileMD5Buffer = md5Array.toString().getBytes("UTF-8");
            return cryptor.calMD5Value(appFileMD5Buffer);
        } catch (UnsupportedEncodingException e) {
            XLog.e(CLASS_NAME, "handleMd5Array error");
            e.printStackTrace();
            return null;
        }
    }

}
