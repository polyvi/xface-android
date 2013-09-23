
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

/**
 * 用来解析schema为1.0的应用配置文件
 */
public class XAppConfigParserSchema1_0 extends XAbstractAppConfigParser {
    private static final String CLASS_NAME = XAppConfigParserSchema1_0.class
            .getSimpleName();

    public XAppConfigParserSchema1_0(Document doc) {
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
        Element appElement = getElementByTagName(mDoc, TAG_APP);
        String id = appElement.getAttribute(ATTR_ID);
        if (XStringUtils.isEmptyString(id)) {
            throw new XTagNotFoundException(ATTR_ID);
        }
        mAppInfo.setAppId(id);
        Element descriptionElement = getElementByTagName(mDoc, TAG_DESCRIPTION);
        mAppInfo.setEntry(getElementValueByAttribute(descriptionElement,
                TAG_ENTRY, ATTR_SRC));
    }

    /**
     * 解析app.xml中的可选的标签
     */
    private void parseOptionalTag() {
        try {
            Element descriptionElement = getElementByTagName(mDoc,
                    TAG_DESCRIPTION);
            parseName(descriptionElement);
            parseType(descriptionElement);
            parseIconBackgroundColor(descriptionElement);
            parseIconSrc(descriptionElement);
            parseVersion(descriptionElement);
            parseMode(descriptionElement);
        } catch (XTagNotFoundException e) {
            XLog.w(CLASS_NAME, "TAG: " + TAG_DESCRIPTION + " Not Found!");
        }
    }

    /**
     * 解析appName
     *
     * @param descriptionElement
     */
    private void parseName(Element descriptionElement) {
        try {
            mAppInfo.setName(getElementValueByNode(descriptionElement, TAG_NAME));
        } catch (XTagNotFoundException e) {
            XLog.w(CLASS_NAME, "TAG: " + TAG_NAME + " Not Config!");
        }
    }

    /**
     * 解析应用类型
     *
     * @param descriptionElement
     */
    private void parseType(Element descriptionElement) {
        try {
            mAppInfo.setType(getElementValueByNode(descriptionElement, TAG_TYPE));
        } catch (XTagNotFoundException e) {
            XLog.w(CLASS_NAME, "TAG: " + TAG_TYPE + " Not Config!");
        }
    }

    /**
     * 解析icon背景颜色
     *
     * @param descriptionElement
     */
    private void parseIconBackgroundColor(Element descriptionElement) {
        try {
            mAppInfo.setIconBackgroudColor(getElementValueByAttribute(
                    descriptionElement, TAG_ICON, ATTR_BACKGROUND_COLOR));
        } catch (XTagNotFoundException e) {
            XLog.w(CLASS_NAME, "TAG: " + TAG_ICON + " Not Config!");
        }
    }

    /**
     * 解析icon源地址
     *
     * @param descriptionElement
     */
    private void parseIconSrc(Element descriptionElement) {
        try {
            String iconSrc = getElementValueByAttribute(descriptionElement,
                    TAG_ICON, ATTR_SRC);
            if (XStringUtils.isEmptyString(iconSrc)) {
                XLog.w(CLASS_NAME, "Attribute: " + ATTR_SRC + " Not Config!");
                return;
            }
            mAppInfo.setIcon(iconSrc);
        } catch (XTagNotFoundException e) {
            XLog.w(CLASS_NAME, "TAG: " + TAG_ICON + " Not Config!");
        }
    }

    /**
     * 解析应用版本号
     *
     * @param descriptionElement
     */
    private void parseVersion(Element descriptionElement) {
        try {
            mAppInfo.setVersion(getElementValueByNode(descriptionElement,
                    TAG_VERSION));
        } catch (XTagNotFoundException e) {
            XLog.w(CLASS_NAME, "TAG: " + TAG_VERSION + " Not Config!");
        }
    }

    /**
     * 解析应用运行模式
     *
     * @param descriptionElement
     */
    private void parseMode(Element descriptionElement) {
        try {
            String runMode = getElementValueByAttribute(descriptionElement,
                    TAG_APP_RUNNING_MODE, ATTR_VALUE);
            if (XStringUtils.isEmptyString(runMode)) {
                XLog.w(CLASS_NAME, "Attribute: " + ATTR_VALUE + " Not Config!");
                return;
            }
            mAppInfo.setRunModeConfig(runMode);
        } catch (XTagNotFoundException e) {
            XLog.w(CLASS_NAME, "TAG: " + TAG_APP_RUNNING_MODE + " Not Config!");
        }
    }
}
