
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

/**
 * 与xml有关的工具类
 */
public class XXmlUtils {
    private static final String CLASS_NAME = XXmlUtils.class.getSimpleName();
    private static final String TAG_PREFERENCE = "preference";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";

    /**
     * 解析xmlPath对应的xml文件，返回Document对象 如果失败，返回null
     */
    public static Document parseXml(String xmlPath) {
        try {
            File configFile = new File(xmlPath);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            InputStream is = new FileInputStream(configFile);
            InputSource source = new InputSource(is);
            Document doc = builder.parse(source);
            is.close();
            return doc;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME, "Xml file " + xmlPath + " does not exist!");
        } catch (IOException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME, "Read file: " + xmlPath + " or parse file: "
                    + xmlPath + " failed!");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME, "Create document builder failed!");
        } catch (SAXException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME, "Parse config file: " + xmlPath + " failed!");
        }
        return null;
    }

    /**
     * 将Document对应的文档结构存入到xml配置文件中
     */
    public static void saveDocToFile(Document doc, String filePath,
            boolean tryEncrypt) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            NodeList nodeList = doc.getChildNodes();
            int len = nodeList.getLength();
            for (int i = 0; i < len; i++) {
                Node node = nodeList.item(i);
                serializeNode(serializer, node);
            }
            serializer.endDocument();
            FileOutputStream fos = new FileOutputStream(filePath);
            String xmlStr = writer.toString();
            fos.write(xmlStr.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME, "Can't write xml file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归的序列化一个结点及其子结点，目前只对Element和Text进行操作
     *
     * @param serializer
     * @param node
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private static void serializeNode(XmlSerializer serializer, Node node)
            throws IllegalArgumentException, IllegalStateException, IOException {
        if (node instanceof Element) {
            Element element = (Element) node;
            String tag = element.getTagName();
            serializer.startTag(null, tag);
            XXmlUtils.serializeAttributeOf(serializer, element);

            NodeList nodeList = element.getChildNodes();
            if (null != nodeList) {
                int len = nodeList.getLength();
                for (int i = 0; i < len; i++) {
                    serializeNode(serializer, nodeList.item(i));
                }
            }

            serializer.endTag(null, tag);
            serializer.text("\n");
        } else if (node instanceof Text) {
            serializer.text(node.getNodeValue());
        }
    }

    /**
     * 序列化一个Element的属性
     *
     * @param serializer
     * @param element
     * @throws IOException
     */
    private static void serializeAttributeOf(XmlSerializer serializer,
            Element element) throws IOException {
        NamedNodeMap map = element.getAttributes();
        for (int i = 0; i < map.getLength(); i++) {
            Node attr = map.item(i);
            serializer.attribute(null, attr.getNodeName(), attr.getNodeValue());
        }
    }

    /**
     * 解析preference标签中相应name的属性值
     * <preference name="type" readonly="true" value="xapp" />
     *
     * @param attrName
     * @return value
     */
    public static String parsePrefValue(Document doc, String attrName) {
        NodeList nodes = doc.getElementsByTagName(TAG_PREFERENCE);
        int len = (null == nodes ? 0 : nodes.getLength());
        for (int i = 0; i < len; i++) {
            Node node = nodes.item(i);
            if (null != node) {
                String parseName = ((Element) node)
                        .getAttribute(ATTR_NAME);
                if (parseName.equals(attrName)) {
                    return ((Element) node).getAttribute(ATTR_VALUE);
                }
            }
        }
        return null;
    }
}
