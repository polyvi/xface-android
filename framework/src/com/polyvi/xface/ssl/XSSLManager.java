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
package com.polyvi.xface.ssl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import android.content.Context;

import com.polyvi.xface.configXml.XCertificateConifgParser;
import com.polyvi.xface.configXml.XXmlOperatorFactory;
import com.polyvi.xface.util.XBase64;
import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XStringUtils;

/**
 * 管理与https有关证书库（KeyStore）及context对象
 */
public class XSSLManager {
    private static final String CLASS_NAME = XSSLManager.class.getSimpleName();
    private static final String CLIENT_CERTIFICATE_NAME = "client.p12";
    private static final String CERTIFACATE_ALGORITHM = "PKCS12";
    private static final String CERTIFICATE_FORMAT = "X509";
    private static final String CERTIFICATE_KEY_FILENAME = "CertificateKey.xml";
    private static final String TLS_PROTOCAL_NAME = "TLS";

    private static XSSLManager instance = null;

    private Context mContext;
    // 保存客户端证书信息
    private XClientCertificate mClientCert;
    private SSLContext mSslContext;
    // 存储客户端证书的证书库
    private KeyStore mKeyStore;

    public static void createInstance(Context ctx) {
        if (instance == null) {
            instance = new XSSLManager(ctx);
        }
    }

    public static XSSLManager getInstace() {
        return instance;
    }

    private XSSLManager(Context ctx) {
        this.mContext = ctx;
        init();
    }

    /**
     * 初始化客户端证书 指定客户端证书进行身份验证.
     */
    private void init() {
        if (!isClientAuthenticationNeeded()) {
            return;
        }
        String password = getCertificatePassword();
        if (null == password) {
            XLog.e(CLASS_NAME, "client certificate password cant't be null");
            return;
        }
        this.mClientCert = new XClientCertificate(CLIENT_CERTIFICATE_NAME,
                password, CERTIFACATE_ALGORITHM);

        mKeyStore = this.createKeyStore(this.mClientCert);
        mSslContext = this.createSSLContext();

        try {
            this.configureHttpsConnection();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME, "initClientCertificate fail.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME,
                    "initClientCertificate fail caused by class not found.");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME,
                    "initClientCertificate fail caused by field access failed.");
        }

    }

    private String getCertificatePassword() {
        XCertificateConifgParser parser = XXmlOperatorFactory
                .createCertificateConifgParser();
        byte[] buffer = new byte[2*XConstant.BUFFER_LEN];
        try {
            InputStream is = mContext.getAssets()
                    .open(CERTIFICATE_KEY_FILENAME);
            int length = is.read(buffer);
            byte[] decryptData = decryptKey(buffer, length);
            parser.setInput(new ByteArrayInputStream(decryptData));
            String password = parser.parseConfig();
            is.close();
            return password;
        } catch (IOException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            return null;
        }
    }

    /**
     * 判断是否需要支持客户端验证
     *
     * @return
     */
    private boolean isClientAuthenticationNeeded() {
        try {
            for (String fileName : mContext.getAssets().list("")) {
                if (fileName.equals(CLIENT_CERTIFICATE_NAME)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;

    }

    /**
     * 创建证书
     *
     * @param certificate
     * @return
     */
    private KeyStore createKeyStore(XClientCertificate certificate) {
        InputStream certificateFileStream = getCertificateFileStream(certificate
                .getCertificateName());
        if (null == certificateFileStream) {
            return null;
        }
        try {
            KeyStore keyStore = KeyStore
                    .getInstance(certificate.getAlgorithm());
            String password = certificate.getPassword();
            keyStore.load(certificateFileStream,
                    password != null ? password.toCharArray() : null);
            return keyStore;
        } catch (KeyStoreException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            return null;
        } catch (CertificateException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            return null;
        } catch (IOException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            return null;
        } finally {
            try {
                if (null != certificateFileStream) {
                    certificateFileStream.close();
                }
            } catch (IOException e) {
                XLog.e(CLASS_NAME, e.getMessage());
                return null;
            }
        }
    }

    /**
     * 获取证书对应的流
     *
     * @param certificateName
     *            证书名称
     * @return
     */
    private InputStream getCertificateFileStream(String certificateName) {
        try {
            InputStream certificateFileStream = mContext.getAssets().open(
                    certificateName);
            return certificateFileStream;
        } catch (IOException e) {
            e.printStackTrace();
            XLog.e(CLASS_NAME,
                    "getCertificateFileStream failed" + e.getMessage());
            return null;
        }
    }

    /**
     * 创建SSL工作环境
     *
     * @return
     */
    private SSLContext createSSLContext() {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory
                    .getInstance(CERTIFICATE_FORMAT);
            String password = mClientCert.getPassword();
            keyManagerFactory.init(mKeyStore,
                    password != null ? password.toCharArray() : null);
            SSLContext sslContext = SSLContext.getInstance(TLS_PROTOCAL_NAME);
            sslContext.init(keyManagerFactory.getKeyManagers(),
                    mClientCert.getTrustManagers(), null);
            return sslContext;
        } catch (NoSuchAlgorithmException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            return null;
        } catch (KeyStoreException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            return null;
        } catch (UnrecoverableKeyException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            return null;
        } catch (KeyManagementException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            return null;
        }
    }

    /**
     * 配置HttpsConnection对应SSL工作环境<br/>
     * (android4.0以下有效)
     */
    private void configureHttpsConnection() throws ClassNotFoundException,
            IllegalArgumentException, IllegalAccessException {
        Class<?> httpsConnection = Class
                .forName("android.net.http.HttpsConnection");
        Field[] fieldlist = httpsConnection.getDeclaredFields();
        for (int i = 0; i < fieldlist.length; i++) {
            Field field = fieldlist[i];
            if (field.getName().equals("mSslSocketFactory")) {
                field.setAccessible(true);
                field.set(null, mSslContext.getSocketFactory());
            }
        }
    }

    public SSLContext getSslContext() {
        return mSslContext;
    }

    public KeyStore getKeyStore() {
        return mKeyStore;
    }

    /**
     * 对byte[]进行解密
     * @param data:要解密的数据
     * @param length:解密数据的长度
     * @return ：解密后的数据
     */
    private byte[] decryptKey(byte[] data, int length) {
        if(null == data) {
            return null;
        }
        /**对每个字节最高位清零 &0x7f*/
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < length; i++) {
            byte b = (byte)(data[i] & (byte)0x7f);
            out.write(b);
        }
        String hexData = out.toString();
        /**对上面得到的字符串进行16进制解码*/
        byte[] newData = XStringUtils.hexDecode(hexData);
        /**对上面得到的字符串进行Base64解码*/
        return XBase64.decode(newData, XBase64.NO_WRAP);
    }
}
