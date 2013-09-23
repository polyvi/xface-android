
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

package com.polyvi.xface.core;

import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.core.XISystemContext;

/**
 * 安全检验监听器，主要用于在安全检验时弹出加载对话框
 */
public interface XAppCheckListener {

    /**
     * 安全检验开始时需要做的操作
     * @param app
     * @param ctx
     */
    public void onCheckStart(XApplication app, XISystemContext ctx);
    /**
     * 安全检验成功
     * @param app
     */
    public void onCheckSuccess(XApplication app, XISystemContext ctx);

    /**
     * 安全校验失败
     * @param app
     */
    public void onCheckError(XApplication app, XISystemContext ctx);

}
