
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

import com.polyvi.xface.app.XAppInfo;
import com.polyvi.xface.util.XStringUtils;
import com.polyvi.xface.xmlParser.XXmlParser;

/**
 * 用来产生和控制具体的解析类
 */
public class XAppConfigParser extends XAbstractAppConfigParser{

    private static final String SCHEMA_1_0 = "1.0"; /** < 通过不同的schema的值来区分不同版本的配置文件 */
    private XAbstractAppConfigParser mParserImpl;
    protected XXmlParser mParser;

    public XAppConfigParser()
    {
        mParser = new XXmlParser();
    }


    /**
     * 设置要解析的源
     * @param is
     */
    @Override
    public void setInput(InputStream is)
    {
        mParser.setInput(is);
    }

    /**
     * 对设置的流进行解析
     * @return 解析得到的Document对象
     */
    private void createParserImpl()
    {
        String schemaValue = getSchemaValue(mDoc);
        if(XStringUtils.isEmptyString(schemaValue))
        {
            mParserImpl =new XAppConfigParserNoSchema(mDoc);
        }
        else if(SCHEMA_1_0.equals(schemaValue))
        {
            mParserImpl = new XAppConfigParserSchema1_0(mDoc);
        }
    }
       /**
     * 获得app.xml文件schema标签的值，若schema标签的值为空则Factory产生XAppConfigParserNoSchema
     * @param document 解析app.xml得到的document
     * @return 返回schema标签的值
     */
    private String getSchemaValue(Document document) {
        Element configElement = (Element) document.getElementsByTagName(
                TAG_CONFIG_ROOT).item(0);
        if (null == configElement) {
            return null;
        }
        return configElement.getAttribute(ATTR_SCHEMA);
    }

    @Override
    /**
     * 获得对app.xml解析得到的XAppInfo对象
     */
    public XAppInfo parseConfig(){
        mDoc = mParser.parse();
        if(null == mDoc) {
            return null;
        }
        createParserImpl();
        return mParserImpl.parseConfig();
    }

}
