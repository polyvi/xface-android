
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

package com.polyvi.xface.configXml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cordova.PluginEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XXmlUtils;
import com.polyvi.xface.xmlParser.XXmlParser;

public class XSysConfigParser {
    private static final String CLASS_NAME = XSysConfigParser.class.getName();
    protected XXmlParser mParser;

    protected static final String TAG_APP_PACKAGE = "app_package";
    protected static final String TAG_EXTENSION = "extension";
    protected static final String TAG_PLUGIN = "plugin";

    protected static final String ATTR_NAME = "name";
    protected static final String ATTR_VALUE = "value";
    protected static final String ATTR_ID = "id";
    protected static final String ATTR_LOG_LEVEL = "LogLevel";
    protected static final String ATTR_FULLSCREEN = "FullScreen";
    protected static final String ATTR_WORK_DIR = "WorkDir";
    protected static final String ATTR_ENGINE_VERSION = "EngineVersion";
    protected static final String ATTR_ENGINE_BUILD = "EngineBuild";
    protected static final String ATTR_SPLASH_DELAY = "SplashScreenDelayDuration";
    protected static final String ATTR_SHOW_SPLASH = "ShowSplashScreen";
    protected static final String ATTR_AUTO_HIDE_SPLASH = "AutoHideSplashScreen";
    protected static final String ATTR_UPDATE_ADDRESS = "UpdateAddress";
    protected static final String ATTR_CHECK_UPDATE = "CheckUpdate";
    protected static final String ATTR_LOADURL_TIMEOUT = "LoadUrlTimeout";

    protected Document mDoc;

    public XSysConfigParser() {
        mParser = new XXmlParser();
    }

    /**
     * 设置要解析的源
     *
     * @param is
     *            要解析的流
     */
    public void setInput(InputStream is) {
        mParser.setInput(is);
    }

    /**
     * 设置要解析的源
     *
     * @param inputStr
     *            要解析的字符流
     */
    public void setInput(String inputStr) {
        mParser.setInput(inputStr);
    }

    /**
     * 设置要解析文件的路径
     * @param path
     */
    public void setPath(String path) {
        mParser.setPath(path);
    }

    /**
     * 对设置的源进行解析，得到Document对象
     * @return
     */
    private Document parse() {
        mDoc = mParser.parse();
        return mDoc;
    }

    /**
     * 对config文件进行解析
     *
     * @return
     */
    public XSysConfigInfo parseConfig() throws XTagNotFoundException{
        parse();
        if(null == mDoc)
        {
            throw new XTagNotFoundException("error_format_config_xml");
        }
        XSysConfigInfo sysConfigInfo = new XSysConfigInfo();
        sysConfigInfo.setPreinstallPackages(parsePreInstallPackagesTag());
        sysConfigInfo.setStartAppId(parseStartAppId());
        sysConfigInfo.setLogLevel(XXmlUtils.parsePrefValue(mDoc, ATTR_LOG_LEVEL));
        sysConfigInfo.setFullscreen(XXmlUtils.parsePrefValue(mDoc, ATTR_FULLSCREEN));
        sysConfigInfo.setSplashDelay(XXmlUtils.parsePrefValue(mDoc, ATTR_SPLASH_DELAY));
        sysConfigInfo.setShowSplash(XXmlUtils.parsePrefValue(mDoc, ATTR_SHOW_SPLASH));
        sysConfigInfo.setAutoHideSplash(XXmlUtils.parsePrefValue(mDoc, ATTR_AUTO_HIDE_SPLASH));
        sysConfigInfo.setWorkDir(XXmlUtils.parsePrefValue(mDoc, ATTR_WORK_DIR));
        sysConfigInfo.setEngineVersion(XXmlUtils.parsePrefValue(mDoc, ATTR_ENGINE_VERSION));
        sysConfigInfo.setEngineBuild(XXmlUtils.parsePrefValue(mDoc, ATTR_ENGINE_BUILD));
        sysConfigInfo.setUpdateAddress(XXmlUtils.parsePrefValue(mDoc, ATTR_UPDATE_ADDRESS));
        sysConfigInfo.setUpdateCheck(XXmlUtils.parsePrefValue(mDoc, ATTR_CHECK_UPDATE));
        sysConfigInfo.setLoadUrlTimeout(XXmlUtils.parsePrefValue(mDoc, ATTR_LOADURL_TIMEOUT));
        sysConfigInfo.setPluginsConfig(parsePluginsConfig());
        sysConfigInfo.setPluginDesciptions(parsePluginDesciptions());
        return sysConfigInfo;
    }

    /**
     * 解析config中配置的预安装包
     *  <pre_install_packages>
     *		<app_package>startapp_1350033381203_normal.xpa</app_package>
     *		<app_package>normal.xpa</app_package>
     *  </pre_install_packages>
     * @return 预安装包列表
     */
    private List<XPreInstallPackageItem> parsePreInstallPackagesTag() throws XTagNotFoundException {
        NodeList nodes = mDoc.getElementsByTagName(TAG_APP_PACKAGE);
        int len = (null == nodes ? 0 : nodes.getLength());
        if(len == 0)
        {
            throw new XTagNotFoundException(TAG_APP_PACKAGE);
        }
        ArrayList<XPreInstallPackageItem> preinstallPackages = new ArrayList<XPreInstallPackageItem>(len);
        for (int i = 0; i < len; i++) {
            Element packageNode = (Element) nodes.item(i);
            String appId = packageNode.getAttribute("id");
            Node textChild = packageNode.getFirstChild();
            if (textChild == null)
                continue;
            String packageName = textChild.getNodeValue();
            preinstallPackages.add(new XPreInstallPackageItem(packageName,
                    appId));
        }
        if(preinstallPackages.size() == 0)
        {
            throw new XTagNotFoundException(TAG_APP_PACKAGE);
        }
        return preinstallPackages;
    }

    /**
     * 解析config中startApp的id,默认规定第一个就是startapp
     * <pre_install_packages>
     *      <app_package id = "app">startapp_1350033381203_normal.xpa</app_package>
     *  </pre_install_packages>
     * @return startApp的id
     * @throws XTagNotFoundException
     */
    private String parseStartAppId(){
        Element packageNode = (Element) mDoc.getElementsByTagName(
                TAG_APP_PACKAGE).item(0);
        if(null == packageNode) {
            XLog.e(CLASS_NAME, "Parse StartApp Id failed!");
            return null;
        }
        return packageNode.getAttribute(ATTR_ID);
    }

    /**
     * 解析config中配置的扩展集合,如：
     * <extensions>
     *     <extension name="Accelerometer" value="XAccelerometerExt" />
     *     <extension name="App" value="XAppExt" />
     * </extension>
     * @return 扩展集合
     */
    private HashMap<String, PluginEntry> parseExtensionTag() {
        NodeList nodes = mDoc.getElementsByTagName(TAG_EXTENSION);
        int len = (null == nodes ? 0 : nodes.getLength());
        HashMap<String, PluginEntry> extensions = new HashMap<String, PluginEntry>(len);
        for (int i = 0; i < len; i++) {
            Node textChild = nodes.item(i);
            if(null != textChild) {
                String name = ((Element) textChild).getAttribute(ATTR_NAME);
                String className = ((Element) textChild).getAttribute(ATTR_VALUE);
                PluginEntry entry = new PluginEntry(name, className, true);
                extensions.put(name, entry);
            }
        }
        return extensions;
    }

    /**
     * 解析加载的插件配置
     * <plugins>
     *   <plugin value = "com.polyvi.external.plugin.XPluginMemory"   name = "T_Memory" />
     * </plugins>
     * @return 加载的插件配置
     */
    private HashMap<String, String> parsePluginsConfig() {
        HashMap<String, String> pluginConfig = new HashMap<String, String>();
        NodeList nodes = mDoc.getElementsByTagName(TAG_PLUGIN);
        int len = (null == nodes ? 0 : nodes.getLength());
        for (int i = 0; i < len; i++) {
            Node textChild = nodes.item(i);
            if(null != textChild) {
                String className = ((Element) textChild).getAttribute(ATTR_VALUE);
                String pluginName = ((Element) textChild).getAttribute(ATTR_NAME);
                pluginConfig.put(className, pluginName);
            }
        }
        return pluginConfig;
    }

    /**
     * 解析加载的插件的描述信息
     * <plugins>
     *   <plugin value = "com.polyvi.external.plugin.XPluginMemory"/>
     * </plugins>
     * @return 加载的插件的描述信息
     */
    private Set<String> parsePluginDesciptions() {
        Set<String> desciptions = new HashSet<String>();
        NodeList nodes = mDoc.getElementsByTagName(TAG_PLUGIN);
        int len = (null == nodes ? 0 : nodes.getLength());
        for (int i = 0; i < len; i++) {
            Node textChild = nodes.item(i);
            if(null != textChild) {
                desciptions.add(((Element) textChild).getAttribute(ATTR_VALUE));
            }
        }
        return desciptions;
    }
}
