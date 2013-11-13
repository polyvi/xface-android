
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

package com.polyvi.xface.extension;

import org.apache.cordova.CordovaWebView;

import com.polyvi.xface.XStartParams;
import com.polyvi.xface.ams.XAMSError.AMS_ERROR;
import com.polyvi.xface.ams.XAppList;
import com.polyvi.xface.ams.XAppManagement;
import com.polyvi.xface.ams.XAppStartListener;
import com.polyvi.xface.ams.XInstallListener;
import com.polyvi.xface.ams.XInstallListener.AMS_OPERATION_TYPE;
import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XPathResolver;
import com.polyvi.xface.view.XAppWebView;

/**
 * 为ams extension需要的ams相关功能提供实现
 */
public class XAmsImpl implements XAms {

    private static final String CLASS_NAME = XAmsImpl.class.getSimpleName();

    private XAppManagement mAppManagement;

    public XAmsImpl(XAppManagement appManagement) {
        this.mAppManagement = appManagement;
    }

    @Override
    public boolean startApp(String appId, String params,
            XAppStartListener startListener) {
        return mAppManagement.startApp(appId, XStartParams.parse(params),
                startListener);
    }

    @Override
    public void installApp(CordovaWebView webContext, String path,
            XInstallListener listener) {
        String workspace = getApp(webContext).getWorkSpace();
        XPathResolver pr = new XPathResolver(path, workspace);
        path = pr.resolve();
        if (path != null) {
            mAppManagement.installApp(path, listener);
        } else {
            XLog.e(CLASS_NAME, "Can't install app in path: " + path
                    + "! Not authorized");
            listener.onError(AMS_OPERATION_TYPE.OPERATION_TYPE_INSTALL, "noId",
                    AMS_ERROR.UNKNOWN);
        }
    }

    @Override
    public void updateApp(CordovaWebView webContext, String path,
            XInstallListener listener) {
        String workspace = getApp(webContext).getWorkSpace();
        XPathResolver pr = new XPathResolver(path, workspace);
        path = pr.resolve();
        if (path != null) {
            mAppManagement.updateApp(path, listener);
        } else {
            XLog.e(CLASS_NAME, "Can't update app in path: " + path
                    + "! Not authorized");
            listener.onError(AMS_OPERATION_TYPE.OPERATION_TYPE_UPDATE, "noId",
                    AMS_ERROR.UNKNOWN);
        }
    }

    @Override
    public void uninstallApp(String appId, XInstallListener listener) {
        mAppManagement.uninstallApp(appId, listener);
    }

    @Override
    public void closeApp(String appId) {
        mAppManagement.closeApp(appId);
    }

    @Override
    public XAppList getAppList() {
        return mAppManagement.getAppList();
    }

    @Override
    public String[] getPresetAppPackages(String startAppWorkSpace) {
        return mAppManagement.getPresetAppPackages(startAppWorkSpace);
    }

    private XApplication getApp(CordovaWebView view) {
        XAppWebView webView = (XAppWebView) view;
        return webView.getOwnerApp();
    }
}
