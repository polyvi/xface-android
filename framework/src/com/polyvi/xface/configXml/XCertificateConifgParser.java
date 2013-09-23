
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

import com.polyvi.xface.util.XLog;
import com.polyvi.xface.xmlParser.XXmlParser;

public class XCertificateConifgParser {
    private static final String CLASS_NAME = XCertificateConifgParser.class.getSimpleName();
    private static final String CERTIFICATE_TAG_NAME = "Certificate";
    private static final String PASSWORD_TAG_NAME = "password";
    protected XXmlParser mParser;
    protected Document mDoc;

    public XCertificateConifgParser()
    {
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
     * 设置要解析文件的路径
     * @param path
     */
    public void setPath(String path) {
        mParser.setPath(path);
    }


    public String parseConfig()
    {
        mDoc = mParser.parse();
        if(null == mDoc)
        {
            XLog.e(CLASS_NAME,"parse CertificateKey.xml Failed.");
            return null;
        }
        Element CertificateElement =(Element) mDoc.getElementsByTagName(CERTIFICATE_TAG_NAME).item(0);
        if(null == CertificateElement)
        {
            XLog.e(CLASS_NAME,"can't find" + CERTIFICATE_TAG_NAME + "TAG" + "in CertificateKey.xml");
            return null;
        }

        Element passwordElement = (Element) CertificateElement.getElementsByTagName(PASSWORD_TAG_NAME).item(0);
        if(null == passwordElement)
        {
            XLog.e(CLASS_NAME,"can't find" + PASSWORD_TAG_NAME + "TAG" + "in CertificateKey.xml");
            return null;
        }

        if( null != passwordElement.getFirstChild())
        {
            String password = passwordElement.getFirstChild().getNodeValue();
            return password;
        }
        return null;

    }
}
