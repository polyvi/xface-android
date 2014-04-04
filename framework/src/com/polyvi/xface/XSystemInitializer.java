
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

import java.io.File;
import java.io.IOException;


import android.content.Context;
import android.content.SharedPreferences;

import com.polyvi.xface.ams.XAMSComponent;
import com.polyvi.xface.ams.XAppList;
import com.polyvi.xface.ams.XIPreInstallListener;
import com.polyvi.xface.ams.XIPreInstallTask;
import com.polyvi.xface.ams.XPreinstalledAppBatchInstaller;
import com.polyvi.xface.app.XAppInfo;
import com.polyvi.xface.app.transferpolicy.XPreInstallAppsTransferPolicy;
import com.polyvi.xface.core.XConfiguration;
import com.polyvi.xface.util.XAppUtils;
import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XFileUtils;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XStrings;

public class XSystemInitializer implements XSystemBootstrap,
        XIPreInstallListener {
    private static final String CLASS_NAME = XSystemInitializer.class.getName();
    private static final String APK_LASTMODIFIED_TIME = "last_modify_time";

    // 这里需要Activity，而不是Context，因为Context#getPackageResourcePath只在ApiLevel8以上支持
    // 而Activity#getPackageResourcePath在api level1就开始支持，现在引擎支持ApiLevel7
    // 的平台，故使用Activity
    private XFaceMainActivity mActivity;
    private boolean mApkUpdate;
    final XAMSComponent mAms;
    private XAppList mAppList;

    public XSystemInitializer(XFaceMainActivity activity) {
        mActivity = activity;
        mAms = createAMSComponent();
    }

    @Override
    public void onSuccess() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                runStartApp();
            }
        });
    }

    @Override
    public void onFailure() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mActivity.toast("Initialize System Failure.");
            }
        });
    }

    @Override
    public void prepareWorkEnvironment() {

        // 如果apk没有更新过且工作目录没有改变过 则不需要做任何准备环境的操作
        mApkUpdate = apkUpdated();
        if (!needPreInstall()) {
            return;
        }
        saveLastModifiedTime();
        // 生成一个.NoMedia 目录
        XFileUtils.createNoMediaFileInWorkDir();
    }

    /**
     * 获取startapp的app.xml文件路径
     *
     * @return
     */
    private String getStartAppConfigPath() {
        // 从配置文件中读取
        return XConstant.PRE_INSTALL_SOURCE_ROOT
                + XConfiguration.getInstance().getStartAppId(mActivity)
                + File.separator + XConstant.APP_CONFIG_FILE_NAME;
    }

    @Override
    public void boot() {
    	mActivity.initStartApp(this.getStartAppInfo());
        Runnable callback = new Runnable() {
            public void run() {
                if (needPreInstall()) {
                    doPreInstall(mAms);
                } else {
                    runStartApp();
                }
            }
        };
        new XStartAppDataInitiallizer(callback, mActivity).execute();
    }

    /**
     * 运行startApp
     */
    private void runStartApp() {
        mActivity.runStartApp();
        mAms.markPortal(mActivity.getStartApp());
        // 后台转移预装应用
        if(XConfiguration.getInstance().isAppTransferNeeded()){
            new XPreInstallAppsTransferPolicy(mAppList, mActivity).transfer();
        }
    }

    /**
     * 读取startapp的app.xml并获取其应用信息
     *
     * @return appInfo
     */
    private XAppInfo getStartAppInfo() {
        XAppInfo startAppInfo = null;
        try {
            startAppInfo = XAppUtils.parseAppXml(mActivity.getAssets().open(
                    getStartAppConfigPath()));
            startAppInfo.setSrcRoot(XConstant.ASSERT_PROTACAL
                    + XConstant.PRE_INSTALL_SOURCE_ROOT
                    + XConfiguration.getInstance().getStartAppId(mActivity));
        } catch (IOException e) {
            XLog.e(CLASS_NAME, "Parse app.xml error!");
            e.printStackTrace();
        } catch (NullPointerException e) {
            XLog.e(CLASS_NAME, "app.xml Config Error.");
            mActivity.toast(XStrings.getInstance().getString(
                    XStrings.APP_CONFIG_ERROR));
        }
        return startAppInfo;
    }

    /**
     * 创建app management
     *
     * @param runtime
     * @return
     */
    private XAMSComponent createAMSComponent() {
        XAMSComponent amsCom = new XAMSComponent(mActivity,
                mActivity.getAppFactory());
        mAppList = amsCom.getAppList();
        return amsCom;
    }

    /**
     * 是否需要预装
     *
     * @return
     */
    private boolean needPreInstall() {
        // 如果apk被更新或者程序的工作目录改变了 都需要重新预装
        // 程序工作目录改变了有可能是由于sdcard的插拔导致 这种情况会导致
        // 用户在程序运行过程生成的部分数据可能被丢失 但是引擎需要保证预装的程序运行正确
        return mApkUpdate
                || XConfiguration.getInstance().isWorkDirectoryChanged();
    }

    /**
     * 开始预装
     *
     * @param runtime
     */
    private void doPreInstall(XAMSComponent ams) {
        XIPreInstallTask task = createPreInstallTask(ams);
        task.run();
    }

    /**
     * 创建预装任务
     *
     * @param ams
     * @return
     */
    private XIPreInstallTask createPreInstallTask(XAMSComponent ams) {
        return new XPreinstalledAppBatchInstaller(mActivity, ams,  this);
    }

    /**
     * 当前程序是否首次安装或者覆盖安装过
     */
    private boolean apkUpdated() {
        String apkPath = mActivity.getPackageResourcePath();
        File sourceFile = new File(apkPath);
        String realLastModifiedTime = Long.toString(sourceFile.lastModified());
        String savedLastModifiedTime = getSavedLastModifiedTime();
        return !realLastModifiedTime.equals(savedLastModifiedTime);
    }

    /**
     * 获取上次存储到配置文件中的apk最后修改时间
     *
     * @return
     */
    private String getSavedLastModifiedTime() {
        SharedPreferences pref = mActivity.getSharedPreferences(
                XConstant.PREF_SETTING_FILE_NAME, Context.MODE_WORLD_READABLE
                        | Context.MODE_WORLD_WRITEABLE);
        String tempTime = pref.getString(APK_LASTMODIFIED_TIME, "");
        return tempTime;
    }

    /**
     * 将当前程序的最后修改时间存到配置文件中
     */
    private void saveLastModifiedTime() {
        String apkPath = mActivity.getPackageResourcePath();
        File sourceFile = new File(apkPath);
        String realLastModifiedTime = Long.toString(sourceFile.lastModified());
        SharedPreferences pref = mActivity.getSharedPreferences(
                XConstant.PREF_SETTING_FILE_NAME, Context.MODE_WORLD_READABLE
                        | Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(APK_LASTMODIFIED_TIME, realLastModifiedTime);
        editor.commit();
    }
}
