
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

import java.util.Iterator;

import android.content.Context;
import android.webkit.WebSettings;

import com.polyvi.xface.XSecurityPolicy;
import com.polyvi.xface.app.XAppInfo;
import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.util.XAppUtils;
import com.polyvi.xface.util.XConstant;

/**
 * 描述App的运行模式
 */
public abstract class XAppRunningMode {

    public enum RUNNING_MODE {
        LOCAL, /** < 本地模式的指令码 */
        ONLINE, /** < 在线模式的指令码 */
        INVALID /** < 无效的模式 */
    };

    /** 本地应用运行模式 */
    private static final String LOCAL_RUNNING_MODE = "local";
    /** 在线应用运行模式 */
    private static final String ONLINE_RUNNING_MODE = "online";

    /**
     * 根据配置串创建具体的运行模式对象
     *
     * @param modeStr
     * @return
     */
    public static XAppRunningMode createAppRunningMode(String modeStr) {
        if (null == modeStr) {
            return null;
        }
        if (modeStr.equals(LOCAL_RUNNING_MODE)) {
            return new XLocalMode();
        }
        if (modeStr.equals(ONLINE_RUNNING_MODE)) {
            return new XOnlineMode();
        }
        return null;
    }

    /**
     * 获得app运行的地址
     *
     * @param app
     *            [in] app对象
     * @return 返回app运行的地址
     */
    public abstract String getAppUrl(XApplication app);

    /**
     * 获得app对应的图标地址
     *
     * @param info
     *            [in] 应用的信息
     * @return 返回图标的地址
     */
    public String getIconUrl(XAppInfo info) {
        String url = XConstant.FILE_SCHEME
                    + XAppUtils.generateAppIconPath(info.getAppId(), info.getIcon());
        return url;
    }

    /**
     * 获取运行模式
     *
     * @return
     */
    public abstract RUNNING_MODE getRunningMode();

    /**
     * 设置appView，使其能够满足HTML5的offline功能
     *
     * @param settings
     *            [in] appView的环境设置
     *
     */
    public void setAppCachedPolicy(WebSettings settings) {
    }

    /**
     * 清除app的缓存数据，例如online模式清除离线缓存数据及localStorage信息
     * @param app[in]
     * @param context[in]
     */
    public void clearAppData(XApplication app, Context context) {
    }

    /**
     * 加载应用
     * @param app[in] 当前应用
     * @param policy[in] 安全策略
     */
    public abstract void loadApp(XApplication app , XSecurityPolicy policy);

    /**
     * 创建资源迭代器
     * @param app[in]
     * @param filter[in]
     * @return
     */
    public Iterator<byte[]> createResourceIterator(XApplication app, XIResourceFilter filter){return null;}
}
