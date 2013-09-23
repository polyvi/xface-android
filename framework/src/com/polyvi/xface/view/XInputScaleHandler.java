
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

package com.polyvi.xface.view;

import java.lang.reflect.Field;

import android.os.Build;
import android.webkit.WebView;

import com.polyvi.xface.util.XLog;
/**
 * 用于屏蔽input框获得焦点时自动放大问题
 */
public class XInputScaleHandler {
    /**
     * 处理input框获得焦点后自动放大的问题，通过反射的方法来控制mDefaultScale的值来屏蔽input框放大的行为
     */
    public static final int APILEVEL_11 = 11;    /** <Android 3.0.x版本API_LEVEL */
    private static final int APILEVEL_13 = 13;  /** <Android 3.2版本API_LEVEL */
    private static final int APILEVEL_16 = 16;  /**<Android 4.1/4.1.1版本API_LEVEL*/
    private static final String CLASS_NAME = XInputScaleHandler.class.getSimpleName();
    private static final int TIME_INTERVAL = 200;/** 根据测试经验连续2次scale change的时间上限 */
    private int mAPILevel;
    private long mLastScaleChangeTime;/** <记录上一次scale change的时间 */
    private float mTransiScale;/** <android4.0一些版本在页面适配的过程中，一部分手机会出现中间态，这个中间态的过渡时间很短，这时不应该调用setDefaultScale去设置
                               mTransiScale变量用来存放scale change过程中的中间态，中间态针对每个手机，且唯一 */
    public XInputScaleHandler() {
        mAPILevel = Build.VERSION.SDK_INT;
    }



    /**
     * 设置android4.0及其以上版本的DefaultScale，android4.0及其以上版本在页面scale时没有中间态，不用排除中间态的处理
     * @param view
     * 缩放处理的view
     * @param oldScale
     * 缩放之前的scale
     * @param newScale
     * 缩放过后的scale
     */
    private void changeDefaultScaleForApiLevel14AndAbove(WebView view,
            float oldScale, float newScale) {
        XAppWebView appView = (XAppWebView) view;
        //在4.0及以上手机 适配的过程没有中间态的过程，故一次设置到位
        setDefaultScaleForApiLevel14AndAbove(newScale,appView);
        /** 设置已经适配完成 */
        appView.setAdapated(true);
    }

    /**
     * 设置android2.3及其以下版本的DefaultScale，android2.3及其以下版本在页面scale时有中间态，需要排除中间态的scale
     * @param view
     * 缩放处理的view
     * @param oldScale
     * 缩放之前的scale
     * @param newScale
     * 缩放过后的scale
     */
    private void changeDefaultScaleForApiLevel10AndBelow(WebView view,
            float oldScale, float newScale) {
        /** 连续2次scale change的时间间隔小于200ms,则认为中间态存在，提取中间态 */
        if((System.currentTimeMillis()-mLastScaleChangeTime)<TIME_INTERVAL && 0 == mTransiScale )
        {
            mTransiScale = oldScale;
        }
        /**适配的scale不是中间态才去设置mDefaultScale的值 */
        if(newScale != mTransiScale || newScale == oldScale)
        {
         setDefaultScaleForApiLevel10AndBelow(newScale, view);
         XAppWebView appView = (XAppWebView)view;
         /** 设置已经适配完成 */
         appView.setAdapated(true);
        }
        mLastScaleChangeTime = System.currentTimeMillis();
    }

    /**
     * 根据android的系统版本改变webview的私有成员变量mDefaultScale
     * @param view
     * webview实例
     * @param oldScale
     * 适配之前的scale
     * @param newScale
     * 适配之后的scale
     */
    public void changeDefaultScale(WebView view, float oldScale, float newScale) {
        // TODO:目前没有处理API LEVEL 在11到13之间的android版本,以及超过APILEVIE16的版本
        if (mAPILevel < APILEVEL_11) {
            changeDefaultScaleForApiLevel10AndBelow(view, oldScale, newScale);
        } else if (mAPILevel > APILEVEL_13 && mAPILevel < APILEVEL_16) {
            changeDefaultScaleForApiLevel14AndAbove(view, oldScale, newScale);
        }
        //剩下的情况为3.x的android系统，直接将Adapated设置为true来屏蔽双击放大
        else
        {
            XAppWebView appView = (XAppWebView)view;
            /** 设置已经适配完成 */
            appView.setAdapated(true);
        }

    }

    /**
     * 设置当前WebView的mDefaultScale为xface控制mOwnDefaulScale,此方法是适用于Android2.3及以下版本
     * @param scale
     * 要设置的scale值
     */
    public void setDefaultScaleForApiLevel10AndBelow(float defaultScale,
            WebView view) {
        try {
            Field mFieldDefaultScale = WebView.class
                    .getDeclaredField("mDefaultScale");
            mFieldDefaultScale.setAccessible(true);
            mFieldDefaultScale.setFloat(view, defaultScale);
        } catch (Exception e) {
            XLog.e(CLASS_NAME, e.toString());
        }
    }

  //TODO:由于是通过反射的方式来修改mDefaultScale的值，在将来的版本中可能需要修改设置的方法
    /**
     * 设置当前WebView的mDefaultScale为xface控制mOwnDefaulScale,由于从android4.0
     * 版本开始改用mZoomManager来管理缩放，所以反射方法不同
     * @param scale
     * 要设置的scale值
     */
    public void setDefaultScaleForApiLevel14AndAbove(float defaultScale,WebView view) {
        try {
            Field mFieldZoomManager = WebView.class
                    .getDeclaredField("mZoomManager");
            mFieldZoomManager.setAccessible(true);
            Object mObjZoomManager = mFieldZoomManager.get(view);
            Class mClassZoomManager = Class
                    .forName("android.webkit.ZoomManager");
            Field mDefaultScale = mClassZoomManager
                    .getDeclaredField("mDefaultScale");
            mDefaultScale.setAccessible(true);
            mDefaultScale.setFloat(mObjZoomManager, defaultScale);
        } catch (Exception e) {
            XLog.e(CLASS_NAME, e.toString());
        }
    }

}
