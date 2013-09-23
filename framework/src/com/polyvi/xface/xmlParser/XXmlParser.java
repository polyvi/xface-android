
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

package com.polyvi.xface.xmlParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.polyvi.xface.util.XLog;

/**
 * xml解析的基类，实现了将流解析成Document对象的接口
 */
public class XXmlParser {
    private static final String CLASS_NAME = XXmlParser.class.getSimpleName();
    protected Document mDoc;
    /** < 解析得到app.xml对应的document对象 */
    protected InputStream mInputStream;

    /**
     * 对输入进行解析
     *
     * @return 解析得到的Document对象
     */
    public Document parse() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            InputSource source = new InputSource(mInputStream);
            mDoc = builder.parse(source);
            mInputStream.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME, "Create document builder failed!");
        } catch (SAXException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME, "Parse InputStream failed!");
        } catch (IOException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME, "IOException in parsing xml!");
        } catch (DOMException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME, "DOMException in parsing xml!");
        }
        return mDoc;
    }

    /**
     * 设置要解析的源
     *
     * @param is
     *            要解析的流
     */
    public void setInput(InputStream is) {
        mInputStream = is;
    }

    /**
     * 设置要解析的源
     *
     * @param inputStr
     *            要解析的字符流
     */
    public void setInput(String inputStr) {
        if(null == inputStr)
        {
            return;
        }
        setInput(new ByteArrayInputStream(inputStr.getBytes()));
    }

    /**
     * 设置要解析的源路径
     *
     * @param path
     *            源路径
     */
    public void setPath(String path) {
        if(null == path)
        {
            return;
        }
        File file = new File(path);
        try {
            setInput(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            XLog.e(CLASS_NAME, "Can't find :" + path);
            e.printStackTrace();
        }
    }
    /**
     * 获取输入流
     * @return
     */
    public InputStream getInputStream()
    {
        return mInputStream;
    }
}
