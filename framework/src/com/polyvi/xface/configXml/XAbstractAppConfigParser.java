
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.polyvi.xface.app.XAppInfo;

/**
 * AppConfigParser的虚基类，定义了一些公共函数和对extensions标签的解析
 */
public abstract class XAbstractAppConfigParser {
    protected XAppInfo mAppInfo;
    protected Document mDoc;
    protected static final String TAG_CONFIG_ROOT = "config";
    protected static final String TAG_APPLICATIONS = "applications";
    protected static final String TAG_ACCESS = "access";
    protected static final String TAG_ORIGIN = "origin";
    protected static final String TAG_SUBDOMAINS = "subdomains";
    protected static final String TAG_WIDGET = "widget";
    protected static final String TAG_CONTENT = "content";
    protected static final String TAG_APP = "app";
    protected static final String TAG_NAME = "name";
    protected static final String TAG_TYPE = "type";
    protected static final String TAG_DESCRIPTION = "description";
    protected static final String TAG_ICON = "icon";
    protected static final String TAG_ENTRY = "entry";
    protected static final String TAG_VERSION = "version";
    protected static final String TAG_DISPLAY = "display";
    protected static final String TAG_WIDTH = "width";
    protected static final String TAG_HEIGHT = "height";
    protected static final String TAG_RUNTIME = "runtime";
    protected static final String TAG_DISTRIBUTION = "distribution";
    protected static final String TAG_PACKAGE = "package";
    protected static final String TAG_SINGLEFILE = "singlefile";
    protected static final String TAG_ENCRYPT = "encrypt";
    protected static final String TAG_CHANNEL = "channel";
    protected static final String TAG_APP_RUNNING_MODE = "running_mode";

    protected static final String ATTR_ID = "id";
    protected static final String ATTR_DEFAULT_APP_ID = "defaultAppId";
    protected static final String ATTR_VERSION = "version";
    protected static final String ATTR_WIDTH = "width";
    protected static final String ATTR_HEIGHT = "height";
    protected static final String ATTR_IS_ENCRYPTED = "isEncrypted";
    protected static final String ATTR_NAME = "name";
    protected static final String ATTR_VALUE = "value";
    protected static final String ATTR_SRC = "src";
    protected static final String ATTR_BACKGROUND_COLOR = "background-color";
    protected static final String ATTR_SCHEMA = "schema";
    protected static final String ATTR_TYPE = "type";
    protected static final String ATTR_APP_SOURCE_DIR = "source_dir";
    protected static final String ATTR_MODE = "mode";

    public XAbstractAppConfigParser() {
          mAppInfo = new XAppInfo();
    }

    abstract public XAppInfo parseConfig();

    public void setInput(InputStream is)
    {

    }

    /**
     * 通过tag名称获取tag对应的元素
     *
     * @param element
     *            上一级tag对应的元素
     * @param tagName
     *            要获取的tag的名称
     * @return tag对应的元素
     * @throws XTagNotFoundException
     */
    protected Element getElementByTagName(Element element, String tagName)
            throws XTagNotFoundException {
        Element targetElement = (Element) element.getElementsByTagName(tagName)
                .item(0);
        if (null == targetElement) {
            throw new XTagNotFoundException(tagName);
        }
        return targetElement;

    }

    /**
     * 通过tag名称获取tag对应的元素,但是tag标签不是必须的
     *
     * @param element
     *            上一级tag对应的元素
     * @param tagName
     *            要获取的tag的名称
     * @return tag对应的元素
     * @throws XTagNotFoundException
     */
    protected Element getElementByTagNameNotNecessary(Element element,
            String tagName) {
        Element targetElement = (Element) element.getElementsByTagName(tagName)
                .item(0);
        if (null == targetElement) {
            return null;
        }
        return targetElement;

    }

    /**
     * 通过tag名称获取tag对应的元素,但是tag标签不是必须的
     *
     * @param document
     *            app.xml对应的document对象
     * @param tagName
     *            要获取的tag的名称
     * @return tag对应的元素
     * @throws XTagNotFoundException
     */
    protected Element getElementByTagNameNotNecessary(Document document,
            String tagName) throws XTagNotFoundException {
        Element targetElement = (Element) document
                .getElementsByTagName(tagName).item(0);
        return targetElement;
    }

    /**
     * 通过tag名称获取tag对应的元素
     *
     * @param document
     *            app.xml对应的document对象
     * @param tagName
     *            要获取的tag的名称
     * @return tag对应的元素
     * @throws XTagNotFoundException
     */
    protected Element getElementByTagName(Document document, String tagName)
            throws XTagNotFoundException {
        Element targetElement = (Element) document
                .getElementsByTagName(tagName).item(0);
        if (null == targetElement) {
            throw new XTagNotFoundException(tagName);
        }
        return targetElement;
    }

    /**
     * 通过tag名称获取tag对应的元素数组
     *
     * @param element
     *            element 上一级tag对应的元素
     * @param tagName
     *            tagName 要获取的tag的名称
     * @return tag对应的元素数组
     * @throws XTagNotFoundException
     */
    protected NodeList getNodeListByTagName(Element element, String tagName)
            throws XTagNotFoundException {
        NodeList targetElement = element.getElementsByTagName(tagName);
        if (null == targetElement) {
            throw new XTagNotFoundException(tagName);
        }
        return targetElement;
    }

    /**
     * 获取tag对应第一个子节点
     *
     * @param element
     *            tag对应的元素
     * @return tag对应第一个子节点
     * @throws XTagNotFoundException
     */
    protected Node getFirstChild(Element element) throws XTagNotFoundException {
        Node node = element.getFirstChild();
        if (null == node) {
            throw new XTagNotFoundException(element.getNodeName());
        }
        return node;
    }

    /**
     * 获取tag元素的值
     *
     * @param parentElement
     *            要获取元素的父类元素
     * @param tag
     *            要获取的元素名称
     * @return 元素对应的值
     * @throws XTagNotFoundException
     */
    protected String getElementValueByNode(Element parentElement, String tag)
            throws XTagNotFoundException {
        Element element = getElementByTagName(parentElement, tag);
        return getFirstChild(element).getNodeValue();
    }

    /**
     * 获取tag元素属性的值
     *
     * @param parentElement
     *            要获取元素的父类元素
     * @param tag
     *            元素的名称
     * @param attribute
     *            属性的名称
     * @return
     * @throws XTagNotFoundException
     */
    protected String getElementValueByAttribute(Element parentElement,
            String tag, String attribute) throws XTagNotFoundException {
        Element element = getElementByTagName(parentElement, tag);
        return element.getAttribute(attribute);
    }
}
