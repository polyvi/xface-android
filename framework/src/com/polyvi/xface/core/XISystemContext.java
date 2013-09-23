
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

package com.polyvi.xface.core;

import org.apache.cordova.CordovaInterface;

import android.app.Activity;
import android.content.Context;

import com.polyvi.xface.XSecurityPolicy;
import com.polyvi.xface.XStartParams;
import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.view.XAppWebView;

public interface XISystemContext {
    /**
     * 在UI线程中运行指定的方法
     */
    public void runOnUiThread(Runnable runnable);

    /**
     * 获取context
     *
     * @return
     */
    public Context getContext();

  
    /**
     * 弹出Toast提示框
     */
    public void toast(String message);

    /**
     * app退出
     */
    public void finish();

    /**
     * 获取启动的参数
     *
     * @return 启动参数
     */
    public XStartParams getStartParams();

    /**
     * 获得activity对象
     */
    public Activity getActivity();

    /**
     * 获取安全策略
     *
     * @return 安全策略
     */
    public XSecurityPolicy getSecurityPolily();
    
    public XApplication getStartApp();
    
    public XAppWebView getCurAppView();
   
    public void runStartApp();
    
    public void loadView(XApplication app, String url);
    
    public void unloadView(XAppWebView view);

    public CordovaInterface getCordovaInterface();
}
