
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

import java.util.HashMap;

/**
 * 描述一个系统事件
 */
public class XEvent extends HashMap<Object, Object> {
    private static final long serialVersionUID = 1L;

    private int mType;
    private Object mData;

    public XEvent() {
        this(0, null);
    }

    public XEvent(int type) {
        this(type, null);
    }

    public XEvent(int type, Object data) {
        mData = data;
        mType = type;
    }

    /**
     * 创建事件
     * @param type
     * @return
     */
    public static XEvent createEvent(int type) {
        return new XEvent(type);
    }

    /**
     * 创建事件
     * @param type
     * @param data
     * @return
     */
    public static XEvent createEvent(int type, Object data) {
        return new XEvent(type, data);
    }

    /**
     * 获取事件类型
     * @return
     */
    public int getType() {
        return mType;
    }

    /**
     * 设置事件数据
     * @param data
     */
    public void setData(Object data) {
        mData = data;
    }

    /**
     * 获取事件数据
     * @return
     */
    public Object getData() {
        return mData;
    }
}
