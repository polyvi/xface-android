
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
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import com.polyvi.xface.util.XBase64;
import com.polyvi.xface.util.XCryptor;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XUtils;

/**
 *加密app.xml的parser对象
 */
public class XSecurityAppConfigParser extends XAppConfigParser {
    private static final String CLASS_NAME = XSecurityAppConfigParser.class.getSimpleName();
    //加密算法名称
    private static final String RSA_ALGORITHM = "RSA";
    //传入的公钥
    private static RSAPublicKey mPublicKey;

    /**
     * 构造函数，初始化公钥
     *
     * @param publicKeyForAppConfig[in] app.xml的公钥字符串
     */
    public XSecurityAppConfigParser(String publicKeyForAppConfig) {
        super();
        initPublicKey(publicKeyForAppConfig);
    }

    /**
     * 根据传入的公钥字符串生成公钥
     *
     * @param publicKeyForAppConfig[in] app.xml的公钥字符串
     */
    private void initPublicKey(String publicKeyForAppConfig) {
        if(null == publicKeyForAppConfig) {
            mPublicKey = null;
            return;
        }
        try {
            byte[] keyBytes = XBase64.decode(publicKeyForAppConfig, 0);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            mPublicKey = (RSAPublicKey)keyFactory.generatePublic(x509KeySpec);
        } catch (NoSuchAlgorithmException e) {
            XLog.d(CLASS_NAME, "Get RSA algorithm error!");
            mPublicKey = null;
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            XLog.d(CLASS_NAME, "Generate RSA public key error!");
            mPublicKey = null;
            e.printStackTrace();
        }
    }

    /**
     *将解密内容设置到inputStream中
     *
     * @param is[out] 加密的输入流
     */
    @Override
    public void setInput(InputStream is) {
        XCryptor cyptor = new XCryptor();
        try {
            byte[] encryptData = XUtils.readBytesFromInputStream(is);
            is = cyptor.decryptByPublicKey(encryptData, mPublicKey);
            super.setInput(is);
        } catch (Exception e) {
            XLog.d(CLASS_NAME, "Decrypt inputStream failed!");
            e.printStackTrace();
            super.setInput(null);
        }
    }
}
