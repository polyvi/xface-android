
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



public class XLogController {
    private static XDefaultLog mDefaultLog = new XDefaultLog();
    public static XSocketLog mSocketLog;

    /**
     * 如果有debug.xml这个配置文件，并且设置了ip则调用此构造函数
     * @param hostIP
     */
    public static void setIP(String hostIP)
    {
        mSocketLog = new XSocketLog(hostIP);
    }

    /**
     * 以VERBOSE类型输出log信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    public static void v(String tag, String s)
    {
        mDefaultLog.v(tag, s);
        if(null != mSocketLog)
        {
            mSocketLog.v(tag, s);
        }
    }

    /**
     * 以DEBUG类型输出log信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    public static void d(String tag, String s)
    {
        mDefaultLog.d(tag, s);
        if(null != mSocketLog)
        {
            mSocketLog.d(tag, s);
        }
    }

    /**
     *
     * 以INFO类型输出log信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    public static void i(String tag, String s)
    {
        mDefaultLog.i(tag, s);
        if(null != mSocketLog)
        {
            mSocketLog.i(tag, s);
        }
    }

    /**
     *
     * 以WARN类型输出log信息.
     *
     * @param tag
     *            log信息的tag名称
     *
     * @param s
     *            需要输出的log信息
     */
    public static void w(String tag, String s)
    {
        mDefaultLog.w(tag, s);
        if(null != mSocketLog)
        {
            mSocketLog.w(tag, s);
        }
    }

    /**
     * 以ERROR类型输出log信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    public static void e(String tag, String s)
    {
        mDefaultLog.e(tag, s);
        if(null != mSocketLog)
        {
            mSocketLog.e(tag, s);
        }
    }

    /**
     * 关闭log
     */
    public static void close()
    {
        if(null != mSocketLog)
            mSocketLog.close();
    }

}
