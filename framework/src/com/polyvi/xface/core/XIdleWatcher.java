
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

import java.util.Timer;
import java.util.TimerTask;
import com.polyvi.xface.util.XLog;

/**
 * 用于监视 应用空闲状态
 *
 */
public class XIdleWatcher extends Timer {

    private static final String CLASS_NAME = XIdleWatcher.class.getSimpleName();
    private TimerTask mTimerTask;
    private Runnable mRunnable;
    private long mInterval;

    public XIdleWatcher() {
    }

    /**
     * 开始监视
     *
     * @param interval
     */
    public void start(long interval, final Runnable task) {
        if (null == mTimerTask) {
            mInterval = interval;
            mRunnable = task;
            scheduleTask(interval, task);
        }
    }

    /**
     * 安排一个任务的执行
     *
     * @param task
     */
    private void scheduleTask(long interval, final Runnable task) {
        mTimerTask = new TimerTask() {
            public void run() {
                XLog.d(CLASS_NAME, "app isn't active.");
                task.run();
            }
        };
        schedule(mTimerTask, interval, interval);
    }

    /**
     * 重置timer
     */
    private void reset() {
        if (null != mTimerTask) {
            mTimerTask.cancel();
            scheduleTask(mInterval, mRunnable);
        }
    }

    /**
     * 停止监视
     */
    public void stop() {
        if (null != mTimerTask) {
            super.cancel();
        }
    }

    /**
     * 应用被操作的时候被调用
     */
    public  void notifyOperatered() {
        reset();
    }
}
