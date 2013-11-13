
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

import com.polyvi.xface.ams.XAMSError.AMS_ERROR;

public interface XAppStartListener {

    /**
     * 启动失败，返回错误信息
     *
     * @param appId
     * @param errorState
     *            错误码
     */
    public void onError(String appId, AMS_ERROR errorState);

    /**
     * 启动成功
     *
     * @param appId
     *
     */
    public void onSuccess(String appId);

}
