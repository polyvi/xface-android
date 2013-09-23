
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

package com.polyvi.xface.util;

import android.util.Log;

/**
 * 负责打印log信息到LogCat中
 */
public class XLog {
    public static final int VERBOSE = Log.VERBOSE;
    public static final int DEBUG = Log.DEBUG;
    public static final int INFO = Log.INFO;
    public static final int WARN = Log.WARN;
    public static final int ERROR = Log.ERROR;
    // Current xxLog level
    private static int xxLogLEVEL = Log.DEBUG;

    public static final String TAG_NAME = "xface";

    private static final String TAG_DEBUG = "DEBUG";

    /**
     * 构造输出log信息
     *
     * @param className
     *            打印log的class名称
     * @param info
     *            用户输出log
     * @return
     */
    private static String constructLogMessage(String className, String info) {
        return " [" + className + "] " + info;
    }

    /**
     * 以VERBOSE类型输出log等级高于xxLog的log信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     */
    public static void v(String className, String s) {
        if (isEnable(Log.VERBOSE)) {
            XLogController.d(TAG_NAME, s);
        }
    }

    /**
     * 以DEBUG类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     */
    public static void d(String className, String s) {
        if (isEnable(Log.DEBUG)) {
            XLogController.d(TAG_NAME, constructLogMessage(className, s));
        }
    }

    /**
     *
     * 以INFO类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     */
    public static void i(String className, String s) {
        if (isEnable(Log.INFO)) {
            XLogController.i(TAG_NAME, constructLogMessage(className, s));
        }
    }

    /**
     *
     * 以WARN类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     */
    public static void w(String className, String s) {
        if (isEnable(Log.WARN)) {
            XLogController.w(TAG_NAME, constructLogMessage(className, s));
        }
    }

    /**
     *
     * 以ERROR类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     */
    public static void e(String className, String s) {
        if (isEnable(Log.ERROR)) {
            XLogController.e(TAG_NAME, constructLogMessage(className, s));
        }
    }

    /**
     *
     * 以VERBOSE类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     * @param e
     *            向控制台抛出的异常
     */
    public static void v(String className, String s, Throwable e) {
        if (isEnable(Log.VERBOSE)) {
            XLogController.v(TAG_NAME, constructLogMessage(className, s));
        }
    }

    /**
     *
     * 以DEBUG类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     * @param e
     *            向控制台抛出的异常
     */
    public static void d(String className, String s, Throwable e) {
        if (isEnable(Log.DEBUG)) {
            XLogController.d(TAG_NAME, constructLogMessage(className, s));
        }
    }

    /**
     *
     * 以INFO类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     * @param e
     *            向控制台抛出的异常
     */
    public static void i(String className, String s, Throwable e) {
        if (isEnable(Log.INFO)) {
            XLogController.i(TAG_NAME, constructLogMessage(className, s));
        }
    }

    /**
     *
     * 以WARN类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     * @param e
     *            向控制台抛出的异常
     */
    public static void w(String className, String s, Throwable e) {
        if (isEnable(Log.WARN)) {
            XLogController.w(TAG_NAME, constructLogMessage(className, s));
        }
    }

    /**
     *
     * 以ERROR类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     * @param e
     *            向控制台抛出的异常
     */
    public static void e(String className, String s, Throwable e) {
        if (isEnable(Log.ERROR)) {
            XLogController.e(TAG_NAME, constructLogMessage(className, s));
        }
    }

    /**
     *
     * 按照args给出的输出形式，以VERBOSE类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     * @param args
     *            输出格式控制参数
     */
    public static void v(String className, String s, Object... args) {
        if (isEnable(Log.VERBOSE)) {
            XLogController.v(TAG_NAME,
                    constructLogMessage(className, String.format(s, args)));
        }
    }

    /**
     *
     * 按照args给出的输出形式，以DEBUG类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     * @param args
     *            输出格式控制参数
     */
    public static void d(String className, String s, Object... args) {
        if (isEnable(Log.DEBUG)) {
            XLogController.d(TAG_NAME,
                    constructLogMessage(className, String.format(s, args)));
        }
    }

    /**
     *
     * 按照args给出的输出形式，以INFO类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     * @param args
     *            输出格式控制参数
     */
    public static void i(String className, String s, Object... args) {
        if (isEnable(Log.INFO)) {
            XLogController.i(TAG_NAME,
                    constructLogMessage(className, String.format(s, args)));
        }
    }

    /**
     *
     * 按照args给出的输出形式，以WARN类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     * @param args
     *            输出格式控制参数
     */
    public static void w(String className, String s, Object... args) {
        if (isEnable(Log.WARN)) {
            XLogController.w(TAG_NAME,
                    constructLogMessage(className, String.format(s, args)));
        }
    }

    /**
     *
     * 按照args给出的输出形式，以ERROR类型输出log等级高于 xxLog的信息.
     *
     * @param className
     *            调用此接口的类名
     * @param s
     *            需要输出的log信息
     * @param args
     *            输出格式控制参数
     */
    public static void e(String className, String s, Object... args) {
        if (isEnable(Log.ERROR)) {
            XLogController.e(TAG_NAME,
                    constructLogMessage(className, String.format(s, args)));
        }
    }

    /**
     * 关闭log
     */
    public static void close() {
        XLogController.close();
    }

    /**
     *
     * 比较当前调用时的log等级与xface设置的当前log等级
     *
     * @param callLevel
     *            当前调用时的log等级
     * @return 当前调用时的log等级高于xface设置的当前log返回true，否则返回false
     */
    private static boolean isEnable(int callLevel) {
        return callLevel >= xxLogLEVEL;
    }

    /**
     * 设置 Log level等级
     *
     * @param level
     *            level等级
     */
    public static void setLogLevel(String level) {
        xxLogLEVEL = level.equalsIgnoreCase(TAG_DEBUG) ? VERBOSE : ERROR;
    }
}
