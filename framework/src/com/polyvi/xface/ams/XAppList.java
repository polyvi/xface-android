package com.polyvi.xface.ams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.polyvi.xface.app.XAppInfo;
import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.app.XApplicationCreator;
import com.polyvi.xface.app.XIApplication;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.view.XAppWebView;

/**
 * 所有app的封装类
 */
public class XAppList {

    private static final String CLASS_NAME = XAppList.class.getSimpleName();

    private List<XIApplication> mAppList;

    public XAppList() {
        mAppList = Collections.synchronizedList(new ArrayList<XIApplication>());
    }

    /**
     * 添加一个app
     *
     * @param app
     */
    public void add(XIApplication app) {
        if (null != app) {
            mAppList.add(app);
        }
    }

    /**
     * 获得viewid对应的app
     *
     * @param viewId
     * @return
     */
    public XApplication getAppByViewId(int viewId) {
        String appId = getAppIdByViewId(viewId);
        XIApplication app = getAppById(appId);
        if (null == app) {
            XLog.w(CLASS_NAME, "getAppByViewId: get app is null!");
        }
        return XApplicationCreator.toWebApp(app);
    }

    /**
     * 根据app id查找对应的app
     *
     * @param appId
     */
    public XIApplication getAppById(String appId) {
        if (null == appId) {
            XLog.w(CLASS_NAME, "app id is null");
            return null;
        }
        for (XIApplication app : mAppList) {
            if (app.getAppId().equals(appId)) {
                return app;
            }
        }
        XLog.w(CLASS_NAME, "Can't find app by id: " + appId);
        return null;
    }

    /**
     * 获得id对应的appview
     *
     * @param viewId
     *            视图id
     * @return
     */
    public XAppWebView getAppViewById(int viewId) {
        for (XIApplication app : mAppList) {
            if (app instanceof XApplication) {
                XApplication webApp = XApplicationCreator.toWebApp(app);
                if (webApp.getViewId() == viewId) {
                    return webApp.getView();
                }
            }
        }
        return null;
    }

    /**
     * 通过viewId查找appId
     *
     * @param viewId
     * @return 查找到的appId，null表示找不到对应的appId
     */
    public String getAppIdByViewId(int viewId) {
        for (XIApplication app : mAppList) {
            XApplication webApp = XApplicationCreator.toWebApp(app);
            if (null == webApp) {
                continue;
            }
            if (webApp.getViewId() == viewId) {
                return app.getAppId();
            }
        }
        return null;
    }

    /**
     * 获取所有app的总个数
     */
    public int getTotalSize() {
        return mAppList.size();
    }

    /**
     * 通过index获取app
     */
    public XIApplication getAppByIndex(int index) {
        return mAppList.get(index);
    }

    /**
     * 从列表中删除app
     *
     * @param appId
     */
    public void removeAppById(String appId) {
        for (XIApplication app : mAppList) {
            if (app.getAppId().equals(appId)) {
                mAppList.remove(app);
                break;
            }
        }
    }

    /**
     * 更新app
     *
     * @param newApp
     * @param newAppInfo
     */
    public void updateApp(XAppInfo newAppInfo, XIApplication oldApp) {
        oldApp.updateAppInfo(newAppInfo);
    }

    /**
     * 返回一个迭代器，用于遍历所有的app
     *
     * @return
     */
    public Iterator<XIApplication> iterator() {
        return new AppIterator();
    }

    /**
     * 迭代器实现，可以遍历所有的app，是否包含portal可选
     */
    private class AppIterator implements Iterator<XIApplication> {

        private static final int INVALID_INDEX = -1;

        /** 下一个要返回的元素的下标值 */
        private int mCursor;

        /** 上一次返回的元素的下标值，-1表示没有返回过元素 */
        private int mLastRet;

        /** 记录已经返回的元素个数，不包含已经remove掉的元素， */
        private int mCount;

        public AppIterator() {
            mLastRet = INVALID_INDEX;
            mCursor = 0;
            mCount = 0;
        }

        @Override
        public boolean hasNext() {
            return mCount < getTotalSize();
        }

        @Override
        public XIApplication next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            XIApplication app = mAppList.get(mCursor);
            mLastRet = mCursor;
            mCursor++;
            mCount++;
            return app;
        }

        @Override
        public void remove() {
            if (mLastRet < 0) {
                throw new IllegalStateException();
            }

            try {
                mAppList.remove(mLastRet);
                mCursor = mLastRet;
                mCount--;
                mLastRet = INVALID_INDEX;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
