
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

package com.polyvi.xface.exceptionReporter;

import android.content.Context;

/**
 * 负责异常信息的处理
 *
 */
public class XExceptionReporter implements Thread.UncaughtExceptionHandler {
    /**Application的context,在获取设备信息的时候要用到*/
    private Context mContext;
    /**崩溃信息处理类*/
    XExceptionReportHandler mReportHandler;
    /**系统默认的崩溃处理*/
    Thread.UncaughtExceptionHandler mDefaultUEH;
    public XExceptionReporter(Context context) {
        mContext = context;
        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * 处理崩溃异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
         report(ex);
         System.exit(-1);//退出程序
         mDefaultUEH.uncaughtException(thread, ex);
      }

    /**
     * 报告异常信息
     * @param e 程序运行产生的异常信息
     */
    private void report(Throwable ex){
        XCrashInfo crashInfo = new XCrashInfo(ex, mContext);
        mReportHandler = new XExceptionReportHandler();
        mReportHandler.saveCrashReportLocal(crashInfo);
    }
}
