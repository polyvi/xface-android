
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

import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * 用于处理touch事件，目前处理了在设置了viewport时双击放大问题
 */
public class XTouchEventHandler {
    private static final int TIME_INTERVAL = 1000;    /** < 判断为连击的时间间隔，单位毫秒 */
    private long mPreviousTouchTime;   /** < 上一次点击发生的时间 */
    private boolean mAdapted;/** < 用来表示适配是否完成，在适配没完成时，不允许在双击处理中将viewport设置为false */

    public XTouchEventHandler() {
        mPreviousTouchTime = 0;
    }

    /**
     * 处理双击以及多次连击时自动放大的情况
     *
     * @param event 交互事件
     *
     * @param view 事件发生的view
     *
     * @return 如果事件被处理了返回true，否则返回false
     */
    public void handleTouchEvent(MotionEvent event, WebView view) {
        /**如果为双击事件则设置setUseWideViewPort(false)来屏蔽双击放大的行为
         * 如果在切换页面的时候，双击有可能没有适配完成，这时候不应该去设置viewport标志
         **/
        if (isDoubleTouch(event) && isAdapted()) {
            view.getSettings().setUseWideViewPort(false);
        }
        /**处理多点触控放大问题：通过检测屏幕上的同时按下的点的个数，如果超过1个则判断为多点触控事件。
         * 当多点触控发生时，使webview的缩放控制失效，同时在下一次onPageStarted中恢复webview的设置
         */
        //TODO:将事件抛到应用，由应用决定是否阻止缩放
        if(event.getPointerCount()>1)
        {
            view.getSettings().setBuiltInZoomControls(false);
            view.getSettings().setSupportZoom(false);
        }
    }

    /**
     * 设置是否适配完成
     * @param adapt true 适配完成 false 适配未完成
     */
    public void setAdapated(boolean adapt)
    {
        mAdapted = adapt;
    }

    /**
     * 获取适配是否完成的标志
     * @return
     */
    public boolean isAdapted()
    {
        return mAdapted;
    }

    /**
     * 判断是否为双击事件
     * @param event
     * 交互事件
     * @return
     */
    private boolean isDoubleTouch(MotionEvent event) {
        boolean ret = false;
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            long lastTouchTime = System.currentTimeMillis();
            /** 连续2次点击的时间间隔小于1s则判断为双击事件 */
            if (0 != mPreviousTouchTime&& (lastTouchTime - mPreviousTouchTime < TIME_INTERVAL)) {
                ret = true;
            } else {
                ret = false;
            }
            mPreviousTouchTime = lastTouchTime;
        }
        return ret;
    }
}
