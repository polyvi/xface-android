
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.polyvi.xface.app.XAppInfo;
import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.app.XApplicationCreator;
import com.polyvi.xface.app.XIApplication;
import com.polyvi.xface.configXml.XAbstractAppConfigParser;
import com.polyvi.xface.configXml.XXmlOperatorFactory;
import com.polyvi.xface.core.XConfiguration;
import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XStringUtils;
import com.polyvi.xface.view.XAppWebView;

/**
 * XAppList的装饰者，负责把应用信息添加、删除、更新到SharedPreferences
 */
public class XPersistentAppList extends XAppList {
    private static final String CLASS_NAME = XPersistentAppList.class.getName();
    /** 分隔符 */
    private static final String DELIMITER = ",:,";
    /** 所有应用的id，例如KEY:appsId VALUE:{app1,:,app2,:,app3} */
    private static final String KEY_APPS_ID = "appsId";
    /** 应用的源路径，例如KEY:app1 VALUE:{source_dirfile:///android_asset/data/app} */
    private static final String TAG_SOURCE_DIR = "source_dir";
    /** 保存应用信息的文件名称 */
    private static final String APPS_FILE_NAME = "apps.pref";

    private XAppList mAppList;
    private SharedPreferences mPreference;
    private XApplicationCreator mCreator;

    public XPersistentAppList(Context ctx, XApplicationCreator creator,
            XAppList appList) {
        mCreator = creator;
        mPreference = ctx.getSharedPreferences(APPS_FILE_NAME,
                Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        mAppList = appList;
        // 读取所有的app到appList
        readApps();
    }

    /**
     * 读取所有的app到appList
     */
    private void readApps() {
        String appsId = mPreference.getString(KEY_APPS_ID, null);
        if (null == appsId) {
            XLog.i(CLASS_NAME, "No apps id in " + APPS_FILE_NAME);
            return;
        }
        String[] appId = appsId.split(DELIMITER);
        for (int index = 0; index < appId.length; index++) {
            try {
                XAppInfo appInfo = readAppInfo(appId, index);
                if (null == appInfo) {
                    continue;
                }
                String appSourceDir = readSourceDir(appId[index]);
                if (null != appSourceDir) {
                    appInfo.setSrcRoot(appSourceDir);
                }
                XIApplication app = mCreator.create(appInfo);
                add(app);
            } catch (FileNotFoundException e) {
                XLog.w(CLASS_NAME, "Can't locate app.xml of app: " + appId[index]);
                e.printStackTrace();
            } catch (IOException e) {
                XLog.w(CLASS_NAME, "IOException in read app.xml of app: "
                        + appId[index]);
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取app.xml文件，获取appInfo
     * @param appId
     * @param index
     * @return
     * @throws IOException, FileNotFoundException
     */
    private XAppInfo readAppInfo(String[] appId, int index)
            throws FileNotFoundException, IOException {
        File appFile = new File(XConfiguration.getInstance()
                .getAppInstallDir(), appId[index] + File.separator
                + XConstant.APP_CONFIG_FILE_NAME);
        FileInputStream fis = new FileInputStream(appFile);
        XAbstractAppConfigParser appConfigParser = XXmlOperatorFactory
                .createAppConfigParser();
        appConfigParser.setInput(fis);
        XAppInfo appInfo = appConfigParser.parseConfig();
        fis.close();
        return appInfo;
    }

    /**
     * 读取应用源码目录
     *
     * @param appId
     * @return 应用源码目录
     */
    private String readSourceDir(String appId) {
        String appAttr = mPreference.getString(appId, null);
        if (null == appAttr) {
            XLog.e(CLASS_NAME, "Can't read " + appId + " in " + APPS_FILE_NAME);
            return null;
        }
        return appAttr.split(TAG_SOURCE_DIR)[1];
    }

    @Override
    public void add(XIApplication app) {
        mAppList.add(app);
        String appsId = mPreference.getString(KEY_APPS_ID, null);
        String appId = app.getAppId();
        if (null == appsId) {
            appsId = appId;
        } else {
            //如果有重复的appid则不增加
            if(contains(appId)) {
                return;
            }
            appsId = appsId + DELIMITER + appId;
        }
        String appAttr = TAG_SOURCE_DIR + app.getAppInfo().getSrcRoot();
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(KEY_APPS_ID, appsId);
        editor.putString(app.getAppId(), appAttr);
        editor.commit();
    }

    @Override
    public XApplication getAppByViewId(int viewId) {
        return mAppList.getAppByViewId(viewId);
    }

    @Override
    public XIApplication getAppById(String appId) {
        return mAppList.getAppById(appId);
    }

    @Override
    public XAppWebView getAppViewById(int viewId) {
        return mAppList.getAppViewById(viewId);
    }

    @Override
    public String getAppIdByViewId(int viewId) {
        return mAppList.getAppIdByViewId(viewId);
    }

    @Override
    public int getTotalSize() {
        return mAppList.getTotalSize();
    }

    @Override
    public XIApplication getAppByIndex(int index) {
        return mAppList.getAppByIndex(index);
    }

    @Override
    public void removeAppById(String appId) {
        mAppList.removeAppById(appId);
        String appsId = mPreference.getString(KEY_APPS_ID, null);
        if(null == appsId || !(contains(appId)) ) {
            XLog.i(CLASS_NAME, "No appsId found in apps.pref");
            return;
        }
        try {
            String[] ids = appsId.split(DELIMITER);
            XStringUtils strUtils = new XStringUtils();
            List<String> appIdList = strUtils.strArrayToList(ids);
            appIdList.remove(appId);
            appsId = strUtils.join(DELIMITER, appIdList);
            SharedPreferences.Editor editor = mPreference.edit();
            editor.putString(KEY_APPS_ID, appsId);
            editor.remove(appId);
            editor.commit();
        } catch(IllegalArgumentException e) {
            XLog.e(CLASS_NAME, "Remove App By Id Error!");
            e.printStackTrace();
        }
    }

    @Override
    public void updateApp(XAppInfo newAppInfo, XIApplication oldApp) {
        mAppList.updateApp(newAppInfo, oldApp);
        String appId = oldApp.getAppId();
        String appsId = mPreference.getString(KEY_APPS_ID, null);
        if (null == appsId) {
            XLog.e(CLASS_NAME, "Update App Error: can't find KEY:"
                    + KEY_APPS_ID + "in" + APPS_FILE_NAME);
            return;
        }
        if (contains(appId)) {
            String newSourceDir = newAppInfo.getSrcRoot();
            String oldSourceDir = readSourceDir(appId);
            if (oldSourceDir == null
                    && newSourceDir != null
                    || (oldSourceDir != null && newSourceDir != null && !oldSourceDir
                            .equals(newSourceDir))) {
                SharedPreferences.Editor editor = mPreference.edit();
                editor.remove(appId);
                newSourceDir = TAG_SOURCE_DIR + newSourceDir;
                editor.putString(appId, newSourceDir);
                editor.commit();
            }
        } else {
            XLog.e(CLASS_NAME, "Update App Error: can't find " + appId);
        }
    }

    /**
     * 判断传递的应用id是否在应用id列表中
     *
     * @param appId
     * @return
     */
    private boolean contains(String appId) {
        if (null == appId) {
            return false;
        }
        String appsId = mPreference.getString(KEY_APPS_ID, null);
        if (null == appsId) {
            return false;
        }
        String[] ids = appsId.split(DELIMITER);
        for (int index = 0; index < ids.length; index++) {
            if (ids[index].equals(appId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<XIApplication> iterator() {
        return mAppList.iterator();
    }

}
