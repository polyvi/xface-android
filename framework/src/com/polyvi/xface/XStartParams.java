
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

package com.polyvi.xface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.polyvi.xface.util.XStringUtils;

/**
 * 启动参数的数据结构
 *
 */
public class XStartParams {

    private XStartParams(String appId, String page, String data) {
        this.appId = appId;
        this.pageEntry = page;
        this.data = data;
    }

    final public String appId;
    final public String pageEntry;
    final public String data;

    /**
     * 解析启动参数 启动参数格式:appid?startpage=a/b.html;data=...
     * 解析结果分为三部分【appId, page, data】
     * @param params
     */
    public static XStartParams parse(String params) {
        if (XStringUtils.isEmptyString(params)) {
            return null;
        }
        String rex = "(((.*?)\\?)?(startpage\\s*=\\s*(.*);)?)?(data\\s*=\\s*)?(.*)";
        Pattern p = Pattern.compile(rex);
        Matcher matcher = p.matcher(params);
        if (matcher.find()) {
            String appId = matcher.group(3);
            String startPage = matcher.group(5);
            String data = matcher.group(7);
            return new XStartParams(appId, startPage, data);
        }
        return null;
    }
}
