
/*
 This file was modified from or inspired by Apache Cordova.

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License. You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied. See the License for the
 specific language governing permissions and limitations
 under the License.
*/

package com.polyvi.xface.view;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;

import android.annotation.TargetApi;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import android.webkit.ClientCertRequestHandler;

import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.ssl.XSSLManager;
import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XLog;

/**
 * 主要实现WebView提供的回调函数
 */
public class XWebViewClient extends CordovaWebViewClient {
    private static final String CLASS_NAME = XWebViewClient.class.getSimpleName();

    public XWebViewClient(CordovaInterface cordova) {
        super(cordova);

    }

    /**
     * Constructor.
     *
     * @param cordova
     * @param view
     */
    public XWebViewClient(CordovaInterface cordova, CordovaWebView view) {
        super(cordova, view);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (url.equals("about:blank")) {
            return;
        }
        XAppWebView currentAppView = (XAppWebView) view;
        XApplication currentApp = currentAppView.mOwnerApp;
        String startParams = (String) currentApp
                .getData(XConstant.TAG_APP_START_PARAMS);
        if (null != startParams) {
            currentApp.removeData(XConstant.TAG_APP_START_PARAMS);
        }
        String jsScript = "try{cordova.require('xFace/privateModule').initPrivateData(['"
                // currentAppId
                + currentApp.getAppId()
                + "','"
                // currentAppWorkspace
                + currentApp.getWorkSpace()
                + "','"
                // appData
                + startParams
                + "']);}catch(e){console.log('exception in initPrivateData:' + e);}";
        currentApp.loadJavascript(jsScript);
        super.onPageFinished(view, url);
    }

    /**
     * 由于服务器的证书可能没有经过CA机构的认证，会出现SslError，通过调用 proceed()来继续SSL连接
     */
    @TargetApi(8)
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler,
            SslError error) {
        handler.proceed();
    }

    /**
     * 在android4.0以下版本直接通过反射方法，将客户端证书设置在SSLContext中 为了在Android 4.x
     * WebView上支持客户端证书，这个方法需要用到WebKit的私有类ClientCertRequestHandler
     * 需要引入jar/cer.jar来解决编译问题
     */
    @TargetApi(14)
    public void onReceivedClientCertRequest(WebView view,
            ClientCertRequestHandler handler, String host_and_port) {
        try {
            KeyStore store = XSSLManager.getInstace().getKeyStore();
            // 未内置客户端证书
            if (store == null) {
                return;
            }
            PrivateKey privateKey = null;
            X509Certificate[] certificates = null;
            Enumeration<String> e = store.aliases();
            while (e.hasMoreElements()) {
                String alias = e.nextElement();
                if (store.isKeyEntry(alias)) {
                    KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) store
                            .getEntry(alias, null);
                    privateKey = entry.getPrivateKey();
                    certificates = (X509Certificate[]) entry
                            .getCertificateChain();
                    break;
                }
            }
            handler.proceed(privateKey, certificates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
