
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;

import com.polyvi.xface.ams.XAMSComponent;
import com.polyvi.xface.app.XAppInfo;
import com.polyvi.xface.core.XConfiguration;
import com.polyvi.xface.util.XAppUtils;
import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XFileUtils;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XLogController;
import com.polyvi.xface.util.XSocketLog;
import com.polyvi.xface.util.XXmlUtils;

/**
 * Player工程 系统启动的实现 目前主要负责：启动一个player app
 */
public class XPlayerSystemBootstrap implements XSystemBootstrap {
    private static final String TAG_START_APP_ID = "app";

    private static final String CLASS_NAME = XPlayerSystemBootstrap.class
            .getSimpleName();

    private static String HOST_PORT = "8018";
    private static int TIME_OUT = 2000;

    private static final String INDEX_HTML_DIR_NAME = "index.html";

    private XFaceMainActivity mContext;
    private String mHostIp;

    public XPlayerSystemBootstrap(XFaceMainActivity context) {
        mContext = context;
    }

    @Override
    public void prepareWorkEnvironment() {
        initLogger();
    }

    /**
     * 配置要启动的app文件
     *
     */
    private void configStartApp() {
        // 先从服务器上获取启动app
        if (configStartAppFromSever()) {
            return;
        }
        // 服务器上获取启动app不成功,再检查本地app是否存在
        String indexPath = getIndexPath();
        if (XFileUtils.checkFileExist(indexPath)) {
            return;
        }
        // 如果本地app不存在,则设置默认的显示页面，防止应用停止到splash界面
        copyAssetsToTargetPath(mContext, INDEX_HTML_DIR_NAME, indexPath);
    }

    /**
     * 从服务器请求startapp
     *
     * @return 从服务器获取的本地文件的地址
     */
    private String requestAppFromServer() {
        String ret = null;
        if (null == mHostIp) {
            // 没有配置IP 请求文件失败
            return ret;
        }
        FileOutputStream fos = null;
        String localFilePath = XConfiguration.getInstance().getWorkDirectory()
                + File.separator + "startapp.zip";
        try {
            // FIXME: 网络部分迁移到XNetworkUtils中去
            URL url = new URL("http://" + mHostIp + ":" + HOST_PORT
                    + "/app.zip");
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(TIME_OUT);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                fos = new FileOutputStream(localFilePath);
                InputStream is = connection.getInputStream();
                byte[] buffer = new byte[XConstant.BUFFER_LEN * 8];
                int length = -1;
                while ((length = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
                fos = null;
                // 从服务器获取文件成功
                ret = localFilePath;
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }


    /**
     * 创建app management
     *
     * @param runtime
     * @return
     */
    private XAMSComponent createAMSComponent() {
        XAMSComponent amsCom = new XAMSComponent(mContext,
                mContext.getAppFactory());
        return amsCom;
    }

    /**
     * 1. 启动一个player app（player app从固定的位置load离散文件）
     */
    @Override
    public void boot() {
        initPlayerApp();
        new Thread() {
            @Override
            public void run() {
                handlePresetAppPackage(mContext.getStartApp().getWorkSpace());
                File appDir = new File(XConfiguration.getInstance()
                        .getAppInstallDir(), TAG_START_APP_ID);
                XFileUtils.copyEmbeddedJsFile(mContext,
                        appDir.getAbsolutePath());
                XFileUtils.createNoMediaFileInWorkDir();
                configStartApp();
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                    	mContext.runStartApp();
                    	XAMSComponent ams = createAMSComponent();
                        ams.markPortal(mContext.getStartApp());
                    }
                });
            }
        }.start();
    }

    /**
     * 初始化 player app
     *
     * @param ams
     *            AppManagement对象
     * */
    private void initPlayerApp() {
        if (!mContext.initStartApp(this.getStartAppInfo())) {
            return;
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
            File file = new File(XConfiguration.getInstance()
                    .getAppInstallDir()
                    + TAG_START_APP_ID
                    + File.separator
                    + XConstant.APP_CONFIG_FILE_NAME);
            startAppInfo = XAppUtils.parseAppXml(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            XLog.w(CLASS_NAME, "app.xml not found!");
        }
        // 如果读取失败，则安装默认方式初始化appInfo
        if (null == startAppInfo) {
            startAppInfo = new XAppInfo();
            startAppInfo.setType("xapp");
        }

        startAppInfo.setAppId(TAG_START_APP_ID);
        startAppInfo.setSrcRoot(XConstant.FILE_SCHEME
                + XConfiguration.getInstance().getAppInstallDir()
                + TAG_START_APP_ID);
        return startAppInfo;
    }

    private void handlePresetAppPackage(String defaultAppWorkspace) {
        String workDir = XConfiguration.getInstance().getWorkDirectory();
        File presetDir = new File(workDir,
                XConstant.PRE_SET_APP_PACKAGE_DIR_NAME);

        if (presetDir.exists()) {
            File defAppWorkspacePresetDir = new File(defaultAppWorkspace,
                    XConstant.PRE_SET_APP_PACKAGE_DIR_NAME);
            // 在默认app的workspace下面创建"pre_set"目录用于存放预置包
            if (!defAppWorkspacePresetDir.exists()) {
                defAppWorkspacePresetDir.mkdirs();
            }
            // 列出pre_set里面的预置包
            String[] presetAppNames = presetDir.list();
            for (String appName : presetAppNames) {
                File oldApp = new File(presetDir.getAbsolutePath(), appName);
                File newApp = new File(
                        defAppWorkspacePresetDir.getAbsolutePath(), appName);
                if (newApp.exists()) {
                    // 如果存在预置包，则将原来的预置包删除
                    newApp.delete();
                }
                boolean success = oldApp.renameTo(newApp);
                if (!success) {
                    XLog.e(CLASS_NAME, "copy pre set app package : " + appName
                            + " fail!");
                }
            }
            // 目前的实现是拷贝结束后删除xface的工作空间里面的预置包目录，以后决定是否需要删除
            XFileUtils.deleteFileRecursively(presetDir.getAbsolutePath());
        }
    }

    /**
     * 从debug.xml中读取socket连接服务器的ip地址
     *
     * @return 返回从debug.xml中读取到的ip地址
     */
    private String readIpFromConfig() {
        /** 配置文件debug.xml内容对应的Document对象 */
        Document Document;
        String xmlPath = XConfiguration.getInstance().getWorkDirectory()
                + XSocketLog.DEBUG_CONFIG;
        File debugXml = new File(xmlPath);
        String hostIP = null;
        if (debugXml.exists()) {
            Document = XXmlUtils.parseXml(xmlPath);
            NodeList nodeList = Document
                    .getElementsByTagName(XSocketLog.TAG_SOCKETLOG);
            if (null == nodeList || 0 == nodeList.getLength()) {
                return null;
            }
            Element socketLogElement = (Element) nodeList.item(0);
            hostIP = socketLogElement.getAttribute(XSocketLog.ATTR_HOST_IP);
        }
        mHostIp = hostIP;
        return hostIP;
    }

    /**
     * 根据是否获取到socket服务器ip地方来对XLog进行初始化
     */
    private void initLogger() {
        String hostIP = readIpFromConfig();
        if (null != hostIP) {
            XLogController.setIP(hostIP);
        }
    }

    /**
     * 从PC服务器请求app文件 return true 成功 false 失败
     */
    private boolean configStartAppFromSever() {
        // TODO:增量请求 加快player的请速度
        String localFile = requestAppFromServer();
        if (null == localFile) {
            XLog.i(CLASS_NAME, "Get AppPath From Server  Fail!");
            return false;
        }
        // 解压到player指定的位置
        if (!XFileUtils.unzipFile(XConfiguration.getInstance()
                .getAppInstallDir() + TAG_START_APP_ID, localFile)) {
            XLog.e(CLASS_NAME, "unzip App From Server  Fail!");
            return false;
        }
        return new File(localFile).delete();
    }

    /**
     * 获取player的index路径
     *
     * @return index路径
     */
    private String getIndexPath() {
        String appInstallDir = XConfiguration.getInstance().getAppInstallDir();
        return (appInstallDir + TAG_START_APP_ID + File.separator + INDEX_HTML_DIR_NAME);
    }

    /**
     * copy assets file to target path
     */
    private void copyAssetsToTargetPath(Context context, String fileName,
            String targetPath) {
        try {
            InputStream is = context.getAssets().open(fileName);
            XFileUtils.createFileByData(new File(targetPath).getAbsolutePath(),
                    is);
            is.close();
        } catch (IOException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            e.printStackTrace();
        }
    }
}
