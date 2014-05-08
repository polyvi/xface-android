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

import java.io.File;
import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.polyvi.xface.XSecurityPolicy;
import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.event.XEvent;
import com.polyvi.xface.event.XEventType;
import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XFileUtils;
import com.polyvi.xface.util.XFileVisitor;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XStringUtils;

/**
 * 在线模式，应对HTML5的offline功能(application cache) 应用的src文件夹（所有源码文件）放在在服务器上
 * app.xml和图片放在在本地
 * 
 */
public class XOnlineMode extends XAppRunningMode implements XAppCheckListener {
    private static final String CLASS_NAME = XOnlineMode.class.getName();
    /** HTML5离线应用缓存的数据库名称 */
    public static final String OFFLINE_DATABASE_NAME = "ApplicationCache.db";
    /** HTML5离线应用缓存的数据库绝对路径 */
    private static String mDataBasePath;
    /** app_database名称 */
    private static final String APP_DATABASE_PATH = "app_database";
    /** localStorage名称 */
    private static final String LOCALSTORAGE = "localstorage";

    @Override
    public String getAppUrl(XApplication app) {
        if (null == app) {
            XLog.e(CLASS_NAME, "Application object is null!");
            return null;
        }
        // 配置的是app的启动路径 http://...
        String entry = app.getAppInfo().getEntry();
        if (entry.contains(XConstant.HTTP_SCHEME)
                || entry.contains(XConstant.HTTPS_SCHEME)) {
            return addPlatformTag(entry);
        }
        app.getSystemContext().toast("app.xml: tag entry config error");
        XLog.e(CLASS_NAME, "app.xml: tag entry config error");
        return null;
    }

    @Override
    public void loadApp(XApplication app, XSecurityPolicy policy) {
        String appUrl = getAppUrl(app);
        if (null == appUrl) {
            XLog.d(CLASS_NAME, "appUrl is null");
            return;
        }
        if (hasAppCache(appUrl, app) && !isOnline(app)) {
            // 有缓存并且无网络时，发送清理http缓存的事件，加载离线缓存资源
            XEvent evt = new XEvent(XEventType.CLEAR_MEMORY_CACHE);
            app.getSystemContext().getEventCenter().sendEventSync(evt);
            policy.checkAppStart(app, this);
        } else {
            app.loadAppIntoView(getAppUrl(app), true);
        }
    }

    @Override
    public RUNNING_MODE getRunningMode() {
        return RUNNING_MODE.ONLINE;
    }

    @Override
    public void clearAppData(XApplication app, Context context) {
        clearOfflineAppCache(getAppUrl(app));
        clearAppLocalStorage(getAppUrl(app), app.getAppId(), context
                .getFilesDir().getParent());
    }

    @Override
    public Iterator<byte[]> createResourceIterator(XApplication app,
            XIResourceFilter filter) {
         return new XOnlineResourceIterator(getAppUrl(app), mDataBasePath, filter);
    }

    /**
     * 判断当前应用是否存在缓存
     *
     * @param appUrl
     *            [in] 当前应用url
     * @param app
     * @return
     */
    private boolean hasAppCache(String appUrl, XApplication app) {
        mDataBasePath = app.getSystemContext().getCordovaInterface()
                .getActivity().getApplicationContext()
                .getDir("database", Context.MODE_PRIVATE).getPath()
                + File.separator + OFFLINE_DATABASE_NAME;
        File file = new File(mDataBasePath);
        boolean hasCache = false;
        if (!file.exists()) {
            XLog.w(CLASS_NAME,
                    "hasAppCache: ApplicationCache.db is not exists!");
            return hasCache;
        }
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
                mDataBasePath, null);
        try {
            String sqlCommand = "select * from CacheResources where id in ( select resource from "
                    + "CacheEntries where cache in( select cache from CacheEntries where "
                    + "resource in ( select id from CacheResources where url = \'"
                    + appUrl + "\')))";
            Cursor cursor = database.rawQuery(sqlCommand, null);
            if (cursor.getCount() > 0) {
                hasCache = true;
            }
            cursor.close();
        } catch (SQLiteException e) {
            XLog.d(CLASS_NAME, "SQLite operate exception!");
            e.printStackTrace();
            return hasCache;
        } finally {
            database.close();
            database = null;
        }
        return hasCache;
    }

    /**
     * 清理当前应用的缓存
     *
     * @param appUrl
     *            [in] 当前应用Url
     */
    public void clearOfflineAppCache(String appUrl) {
        if (null == mDataBasePath) {
            XLog.w(CLASS_NAME,
                    "clearAppCache: ApplicationCache.db is not exists!");
            return;
        }
        File file = new File(mDataBasePath);
        if (!file.exists()) {
            XLog.w(CLASS_NAME,
                    "clearAppCache: ApplicationCache.db is not exists!");
            return;
        }
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
                mDataBasePath, null);
        try {
            String sqlCommand = "select cache from CacheEntries where "
                    + "resource in ( select id from CacheResources where url = \'"
                    + appUrl + "\')";
            Cursor cursor = database.rawQuery(sqlCommand, null);
            if (!cursor.moveToFirst()) {
                XLog.e(CLASS_NAME, "clearAppCache: moveToFirst error!");
                cursor.close();
                return;
            }
            int cacheIndex = cursor.getColumnIndex("cache");
            if (-1 == cacheIndex) {
                XLog.e(CLASS_NAME, "Clear AppCache failed!");
                cursor.close();
                return;
            }
            String cache = cursor.getString(cacheIndex);
            cursor.close();
            // 删除Caches表中当前应用的对应项
            database.delete("Caches", "cacheGroup = \'" + cache + "\'", null);
            // 删除CacheAllowsAllNetworkRequests表中当前应用的对应项
            database.delete("CacheAllowsAllNetworkRequests", "cache = \'"
                    + cache + "\'", null);
            // 删除CacheResources表中当前应用的对应项
            database.delete("CacheResources", "id in ( select resource from "
                    + "CacheEntries where cache = \'" + cache + "\')", null);
            // 删除CacheResourceData表中当前应用的对应项
            database.delete("CacheResourceData",
                    "id in ( select resource from "
                            + "CacheEntries where cache = \'" + cache + "\')",
                    null);
            // 删除CacheEntries表中当前应用的对应项
            database.delete("CacheEntries", "cache = \'" + cache + "\'", null);
            // 删除CacheGroups表中当前应用的对应项
            database.delete("CacheGroups", "newestCache = \'" + cache + "\'",
                    null);
            // 删除CacheWhiltelistURLs表中当前应用的对应项
            database.delete("CacheWhitelistURLs", "cache = \'" + cache + "\'",
                    null);
        } catch (SQLiteException e) {
            XLog.d(CLASS_NAME, "SQLite operate exception!");
            e.printStackTrace();
        } finally {
            database.close();
            database = null;
        }
    }

    /**
     * 清理应用对应的localStorage的键值对
     *
     * @param appUrl
     *            [in] 当前应用Url
     * @param appId
     *            [in] 当前应用的appId
     * @param appPackageDir
     *            [in] 当前应用的appPackageDir 例如:/data/data/com.paas.xface <br/>
     */
    private void clearAppLocalStorage(String appUrl, String appId,
            String appPackageDir) {
        if (XStringUtils.isEmptyString(appUrl)
                || XStringUtils.isEmptyString(appId)
                || XStringUtils.isEmptyString(appPackageDir)) {
            return;
        }
        // 得到数据库的名字
        String localStorageName = getLocalStorageName(appUrl);
        if (XStringUtils.isEmptyString(localStorageName)) {
            return;
        }
        // 清理数据
        LocalStorageClear localStorageClear = new LocalStorageClear(
                localStorageName, appId);
        // 得到app_database路径
        String databasePath = appPackageDir + File.separator
                + APP_DATABASE_PATH + File.separator;
        XFileUtils.walkDirectory(databasePath, localStorageClear);
    }

    /**
     * 清理应用的localstorage信息 内部使用
     */
    class LocalStorageClear implements XFileVisitor {
        private boolean mContinueTraverse = true;
        private String mFileName;
        private String mAppId;

        LocalStorageClear(String fileName, String appId) {
            mFileName = fileName;
            mAppId = appId;
        }

        @Override
        public void visit(String filePath) {
            File file = new File(filePath);
            if (file.getName().equals(mFileName)) {
                mContinueTraverse = false;
                if (!file.exists()) {
                    XLog.d(CLASS_NAME,
                            "clearAppLocalStorage: " + file.getAbsolutePath()
                                    + " is not exists!");
                    return;
                }
                SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
                        file, null);
                try {
                    database.delete("ItemTable", "key in ( select key from "
                            + "ItemTable where key like \'" + mAppId + "%\')",
                            null);
                } catch (SQLiteException e) {
                    XLog.e(CLASS_NAME, "SQLite operate exception!");
                    e.printStackTrace();
                } finally {
                    if (null != database) {
                        database.close();
                        database = null;
                    }
                }
            }
        }

        @Override
        public boolean isContinueTraverse() {
            return mContinueTraverse;
        }
    }

    @Override
    public void onCheckSuccess(XApplication app, XISystemContext ctx) {
        app.loadAppIntoView(getAppUrl(app));
    }

    @Override
    public void onCheckError(XApplication app, XISystemContext ctx) {
        String appUrl = getAppUrl(app);
        clearOfflineAppCache(appUrl);
        app.loadAppIntoView(appUrl);
    }

    @Override
    public void onCheckStart(XApplication app, XISystemContext ctx) {

    }

    /**
     * 获取appUrl对应的localStorage数据库名字
     *
     * @param appUrl
     *            在线应用的URL地址
     *            例如：http://www.polyvi.net:8012/offlineApp/yecx/index.html
     * @return 在线应用所在localstorage数据库 例如:"http_com.polyvi.net_8012.localstorage"
     */
    private String getLocalStorageName(String appUrl) {
        appUrl = appUrl.replaceAll("://", "_");
        appUrl = appUrl.replaceAll(":", "_");
        return appUrl.substring(0, appUrl.indexOf("/")) + "." + LOCALSTORAGE;
    }

    /**
     * 给Url加上平台标示符 example:
     * http://polyvi.com会变成http://polyvi.com?platform=android
     * http://polyvi.com?data
     * =test会变成http://polyvi.com?data=test&platform=android
     */
    private String addPlatformTag(String url) {
        if (null == url) {
            return url;
        }
        String append = url.indexOf("?") > 0 ? "&platform=android"
                : "?platform=android";
        return url + append;
    }

    /**
     * 判断网络是否可用
     *
     * @param app
     * @return
     */
    private boolean isOnline(XApplication app) {
        ConnectivityManager cm = (ConnectivityManager) app.getSystemContext()
                .getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
