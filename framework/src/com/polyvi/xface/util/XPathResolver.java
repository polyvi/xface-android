
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

import java.io.File;
import java.io.IOException;
import org.apache.cordova.CordovaResourceApi;

import android.net.Uri;
/**
 * 把传入的路径解析为对应的相对于app workspace的路径、sdcard路径或其他绝对路径
 */
public class XPathResolver {
    private String mPath;
    private String mAppWorkspace;

    /**
     * @param path
     *            需要解析的路径
     * @param appWorkspace
     *            app的workspace
     */
    public XPathResolver(String path, String appWorkspace) {
        mPath = path;
        mAppWorkspace = appWorkspace;
    }

    /**
     * 解析路径
     *
     * @param resourceApi
     * @return 路径合法就返回在当前系统中对应的路径，否则就返回null。
     *         路径不合法可能是因为以下原因之一：
     *         1、file://...uri不符合格式或主机不存在，目前只有sdcard host和默认的local host
     *         2、sdcard不可访问
     *         3、相对app workspace的路径不在app workspace之内。
     */
    public String resolve(CordovaResourceApi resourceApi) {
        // 根据协议解析路径
        if (null == mPath) {
            return null;
        }
        Uri uri = Uri.parse(mPath);
        if (null != uri.getScheme()) {
            return resourceApi.remapUri(uri).getPath();
        } else if (mPath.startsWith("/")) {
            // 全路径形式，例如：/mnt/sdcard/mypath
            return mPath;
        } else {
            // 相对app workspace的路径，无'/'开头
            return resolveAppRelativePath(mPath);
        }
    }

    /**
     * 解析相对 app workspace 的路径
     *
     * @param path
     *            要解析的路径
     * @return 路径在app workspace之内，则返回该路径的绝对路径，否则返回null。
     */
    private String resolveAppRelativePath(String path) {
        File file = new File(mAppWorkspace, path);
        String absolutePath = null;
        try {
            absolutePath = file.getCanonicalPath();
            // 路径是否在app workspace 之内
            if (!XFileUtils.isFileAncestorOf(mAppWorkspace, absolutePath)) {
                absolutePath = null;
            }
        } catch (IOException e) {
            absolutePath = null;
            e.printStackTrace();
        }
        return absolutePath;
    }

}
