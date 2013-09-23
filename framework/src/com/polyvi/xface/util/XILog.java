
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

public interface XILog {

    /**
     * 以VERBOSE类型输出log等级高于xxLog的log信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    public void v(String tag, String s);

    /**
     * 以DEBUG类型输出log等级高于 xxLog的信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    public void d(String tag, String s);

    /**
     *
     * 以INFO类型输出log等级高于 xxLog的信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    public void i(String tag, String s);

    /**
     *
     * 以WARN类型输出log等级高于 xxLog的信息.
     *
     * @param tag
     *            log信息的tag名称
     *
     * @param s
     *            需要输出的log信息
     */
    public void w(String tag, String s);

    /**
     * 以ERROR类型输出log等级高于 xxLog的信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    public void e(String tag, String s);

    /**
     * 关闭log
     */
    public void close();
}
