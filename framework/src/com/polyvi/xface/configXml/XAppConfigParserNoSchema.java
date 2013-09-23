
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polyvi.xface.app.XAppInfo;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XStringUtils;
import com.polyvi.xface.util.XXmlUtils;

/**
 * 用来解析不含schema标签的应用配置文件
 */
public class XAppConfigParserNoSchema extends XAbstractAppConfigParser {
    private static final String CLASS_NAME = XAppConfigParserNoSchema.class
            .getSimpleName();

    public XAppConfigParserNoSchema(Document doc) {
        super();
        mDoc = doc;
    }

    /**
     * 统一调用的解析接口
     *
     * @return 返回一个XAppInfo对象
     */
    public XAppInfo parseConfig() {
        try {
            parseRequiredTag();
            parseOptionalTag();
            return mAppInfo;
        } catch (XTagNotFoundException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            return null;
        }
    }

    /**
     * 解析app.xml中AppInfo必需的标签
     *
     * @throws XTagNotFoundException
     */
    private void parseRequiredTag() throws XTagNotFoundException {
        Element widgetElement = getElementByTagName(mDoc, TAG_WIDGET);
        String id = widgetElement.getAttribute(ATTR_ID);
        if (XStringUtils.isEmptyString(id)) {
            throw new XTagNotFoundException(ATTR_ID);
        }
        mAppInfo.setAppId(id);
        mAppInfo.setEntry(getElementValueByAttribute(widgetElement,
                TAG_CONTENT, ATTR_SRC));
    }

    /**
     * 解析app.xml中的可选的标签
     */
    private void parseOptionalTag() {
        try {
            Element widgetElement = getElementByTagName(mDoc, TAG_WIDGET);
            parseVersion(widgetElement);
            parseName(widgetElement);
            parseIcon(widgetElement);
        } catch (XTagNotFoundException e) {
            XLog.w(CLASS_NAME, "TAG: " + TAG_WIDGET + " Not Found!");
        }
        parseType();
        parseMode();
    }

    /**
     * 解析应用版本号
     *
     * @param widgetElement
     */
    private void parseVersion(Element widgetElement) {
        String version = widgetElement.getAttribute(ATTR_VERSION);
        if (null == version) {
            XLog.w(CLASS_NAME, "Attribute: " + ATTR_VERSION + " Not Config!");
            return;
        }
        mAppInfo.setVersion(version);
    }

    /**
     * 解析应用名称
     *
     * @param widgetElement
     */
    private void parseName(Element widgetElement) {
        try {
            mAppInfo.setName(getElementValueByNode(widgetElement, TAG_NAME));
        } catch (XTagNotFoundException e) {
            XLog.w(CLASS_NAME, "TAG: " + TAG_NAME + " Not Config!");
        }
    }

    /**
     * 解析icon图片源地址
     *
     * @param widgetElement
     */
    private void parseIcon(Element widgetElement) {
        try {
            mAppInfo.setIcon(getElementValueByAttribute(widgetElement,
                    TAG_ICON, ATTR_SRC));
        } catch (XTagNotFoundException e) {
            XLog.w(CLASS_NAME, "TAG: " + TAG_ICON + " Not Config!");
        }
    }

    /**
     * 解析app类型
     */
    private void parseType() {
        String appType = XXmlUtils.parsePrefValue(mDoc, ATTR_TYPE);
        if (null != appType) {
            mAppInfo.setType(appType);
            return;
        }
        XLog.w(CLASS_NAME, "TAG: " + TAG_ICON + " Not Config!");
    }

    /**
     * 解析app运行模式
     */
    private void parseMode() {
        String mode = XXmlUtils.parsePrefValue(mDoc, ATTR_MODE);
        if (null != mode) {
            mAppInfo.setRunModeConfig(mode);
            return;
        }
        XLog.w(CLASS_NAME, "TAG: " + ATTR_MODE + " Not Config!");
    }

}
