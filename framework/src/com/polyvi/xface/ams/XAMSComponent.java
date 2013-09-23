
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

import org.apache.cordova.PluginEntry;

import com.polyvi.xface.app.XAppInfo;
import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.app.XApplicationCreator;
import com.polyvi.xface.app.XIApplication;
import com.polyvi.xface.core.XISystemContext;
import com.polyvi.xface.extension.XAmsExt;
import com.polyvi.xface.extension.XAmsImpl;
import com.polyvi.xface.view.XAppWebView;
import com.polyvi.xface.view.XStartAppView;

/**
 * ams组件
 */
public class XAMSComponent {

    private XAppManagement mAms;
    private XISystemContext mCtx;

    public XAMSComponent(XISystemContext ctx, XApplicationCreator creator) {
        mAms = new XAppManagement(creator);
        mAms.init(ctx);
        mCtx = ctx;
    }

    public XAppManagement getAppManagement() {
        return mAms;
    }

    /**
     * 获得applist
     *
     * @return
     */
    public XAppList getAppList() {
        return mAms.getAppList();
    }

    /**
     * 根据appid 获得对应的app
     *
     * @param appid
     * @return
     */
    public XIApplication getAppById(String appid) {
        return mAms.getAppList().getAppById(appid);
    }

    /**
     * 注册ams插件到app
     *
     * @param app
     */
    private void registerAMSPluginTo(XApplication app) {
        XAmsExt amsExt = new XAmsExt();
        amsExt.init(new XAmsImpl(mAms), mCtx.getCordovaInterface(), app.getView());
        PluginEntry entry = new PluginEntry("AMS", amsExt);
        app.getView().pluginManager.addService(entry);
    }

    /**
     * 标记portal程序
     *
     * @param app
     */
    public void markPortal(XApplication app) {
        registerAMSPluginTo(app);
        XAppWebView webView = app.getView();
        if( !(webView instanceof XStartAppView)){
        	//log error info
        }else{
        	((XStartAppView)webView).setAppManagement(mAms);
        }
    }

    /**
     * 添加应用
     *
     * @param app
     */
    public void add(XIApplication app) {
        this.getAppList().add(app);
    }

    /**
     * 更新应用
     *
     * @param newAppInfo
     *            新应用的信息
     * @param oldApp
     *            旧的应用
     */
    public void updateApp(XAppInfo newAppInfo, XIApplication oldApp) {
        this.getAppList().updateApp(newAppInfo, oldApp);
    }
}
