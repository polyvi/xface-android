
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

import com.polyvi.xface.app.XIApplication;

/**
 * 本地应用installer的监听,实现对安装过程与js的交互
 */
public class XNativeAppInstallerListener implements XInstallListener {

    private XInstallListener mListener;
    private XNativeAppInstallDelegate mInstaller;
    private XIApplication mApp;

    public XNativeAppInstallerListener(XInstallListener listener,
            XNativeAppInstallDelegate installer) {
        mListener = listener;
        mInstaller = installer;
    }

    public XNativeAppInstallerListener(XInstallListener listener,
            XNativeAppInstallDelegate installer,XIApplication app) {
        mListener = listener;
        mInstaller = installer;
        mApp = app;
    }

    @Override
    public void onProgressUpdated(AMS_OPERATION_TYPE type,
            InstallStatus progressState) {
        mListener.onProgressUpdated(type, progressState);
    }

    @Override
    public void onError(AMS_OPERATION_TYPE type, String appId,
            AMS_ERROR errorState) {
        mListener.onError(type, appId, errorState);
    }

    @Override
    public void onSuccess(AMS_OPERATION_TYPE type, String appId) {
        mListener.onSuccess(type, appId);
        if (type == AMS_OPERATION_TYPE.OPERATION_TYPE_INSTALL) {
            mInstaller.installApp(XNativeAppInstallDelegate.getApkPathFromInstallDir(appId), mListener);
        } else if (type == AMS_OPERATION_TYPE.OPERATION_TYPE_UNINSTALL) {
            mInstaller.uninstallApp(mApp.getAppInfo().getEntry(), mListener);
        } else if(type == AMS_OPERATION_TYPE.OPERATION_TYPE_UPDATE) {
            mInstaller.updateApp(XNativeAppInstallDelegate.getApkPathFromInstallDir(appId), mListener);
        }

    }

}
