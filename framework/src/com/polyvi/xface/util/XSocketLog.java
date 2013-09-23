
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;

public class XSocketLog implements XILog {
    private static final String VERBOSE = "verbose:";
    private static final String DEBUG = "debug:";
    private static final String INFO = "info:";
    private static final String WARN = "warn:";
    private static final String EEROR = "error:";
    private static final String CLOSESIGNAL = "close_signal";
    private Socket mSocket;
    private BufferedWriter mWriter;
    private String mHost;

    /** debug.xml中的tag标签 */
    public static final String TAG_SOCKETLOG = "socketlog";
    public static final String ATTR_HOST_IP = "hostip";
    /** 配置文件名 */
    public static final String DEBUG_CONFIG = "debug.xml";
    /** socketlog默认的服务器端的端口号 */
    public static final int HOST_PORT = 6656;
    /** socket连接超时时间 */
    public static final int mTimeout = 6000;

    /**
     * 构造函数
     *
     * @param host
     *            服务器的ip地址
     * @param port
     *            对应的端口号
     * @return
     */
    public XSocketLog(String host) {
        mHost = host;
        new Thread(new Runnable() {

            @Override
            public void run() {
                mSocket = new Socket();
                try {
                    mSocket.connect(new InetSocketAddress(mHost, HOST_PORT),
                            mTimeout);
                    mWriter = new BufferedWriter(new OutputStreamWriter(mSocket
                            .getOutputStream()));
                } catch (IOException e) {
                    XLogController.mSocketLog = null;
                    Log.d("xface",
                            "please check the hostip in debug.xml or start xFace_console.exe first!");
                }
            }
        }).start();
    }

    /**
     * 以VERBOSE类型输出log等级高于xxLog的log信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    @Override
    public void v(String tag, String s) {
        if (null != mWriter) {
            try {
                mWriter.write(VERBOSE + tag + s.replace("\n", " ") + "\n");
                mWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 以DEBUG类型输出log等级高于 xxLog的信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    @Override
    public void d(String tag, String s) {
        if (null != mWriter) {
            try {
                mWriter.write(DEBUG + tag + s.replace("\n", " ") + "\n");
                mWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * 以INFO类型输出log等级高于 xxLog的信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    @Override
    public void i(String tag, String s) {
        if (null != mWriter) {
            try {
                mWriter.write(INFO + tag + s.replace("\n", " ") + "\n");
                mWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * 以WARN类型输出log等级高于 xxLog的信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    @Override
    public void w(String tag, String s) {
        if (null != mWriter) {
            try {
                mWriter.write(WARN + tag + s.replace("\n", " ") + "\n");
                mWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 以ERROR类型输出log等级高于 xxLog的信息.
     *
     * @param tag
     *            log信息的tag名称
     * @param s
     *            需要输出的log信息
     */
    @Override
    public void e(String tag, String s) {
        if (null != mWriter) {
            try {
                mWriter.write(EEROR + tag + s.replace("\n", " ") + "\n");
                mWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭log
     */
    public void close() {
        if (null != mWriter) {
            try {
                mWriter.write(CLOSESIGNAL);
                mWriter.close();
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
