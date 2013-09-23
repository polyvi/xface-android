
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 对字符串的处理工具类
 */
//TODO:将XUtils中的部分关于字符串处理的工具函数移到本类
public class XStringUtils {
    private static final String CLASS_NAME = XStringUtils.class.getName();

    /**
     * 字符串数组转list
     * @param strAry
     * @return
     */
    public List<String> strArrayToList(String[] strAry) {
        if(null == strAry) {
            XLog.e(CLASS_NAME, "strArrayToList: strAry is null!");
            throw new IllegalArgumentException();
        }
        List<String> list = new ArrayList<String>();
        for (String str : strAry) {
            list.add(str);
        }
        return list;
    }

    /**
     * 将字符串和字符串数组中每个元素合并在一起
     * 例如：join是".",strAry是{1,2,3},合并后是"1.2.3"
     * @param join
     * @param strAry
     * @return
     */
    public String join(String join, List<String> strAry) {
        if(null == join || null == strAry) {
            XLog.e(CLASS_NAME, "join : params is null");
            throw new IllegalArgumentException();
        }
        StringBuffer sb = new StringBuffer();
        int size = strAry.size();
        for (int i = 0; i < size; i++) {
            if (i == (size - 1)) {
                sb.append(strAry.get(i));
            } else {
                sb.append(strAry.get(i)).append(join);
            }
        }
        return sb.toString();
    }

	/**
	 * 将二进制转化为HEX编码 ：将形如0x12 0x2A 0x01 转换为122A01
	 *
	 * @param data
	 * @return
	 */
	public static String hexEncode(byte[] data) {
	    if(null == data) {
	        return null;
	    }
	    StringBuffer buffer = new StringBuffer();
	    for (int i = 0; i < data.length; i++) {
	        String tmp = Integer.toHexString(data[i] & 0xff);
	        if (tmp.length() < 2) {
	            buffer.append('0');
	        }
	        buffer.append(tmp);
	    }
	    String retStr = buffer.toString().toUpperCase();
	    return retStr;
	}

	/**
	 * 将String转化为HEX解码 ：将形如122A01 转换为0x12 0x2A 0x01
	 *
	 * @param data
	 * @return
	 */
	public static byte[] hexDecode(String data) {
	    if(null == data) {
	        return null;
	    }
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    for (int i = 0; i < data.length(); i += 2) {
	        String onebyte = data.substring(i, i + 2);
	        int b = Integer.parseInt(onebyte, 16) & 0xff;
	        out.write(b);
	    }
	    return out.toByteArray();
	}

	/**
	 * 判断是否是空串
	 */
	public static boolean isEmptyString(String str) {
	    return null == str || "".equals(str);
	}
}
