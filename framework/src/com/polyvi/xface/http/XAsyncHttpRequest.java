
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

package com.polyvi.xface.http;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

public class XAsyncHttpRequest implements Runnable {

    private final AbstractHttpClient mClient;
    private final HttpContext mContext;
    private final HttpUriRequest mRequest;
    private final XAsyncHttpResponseHandler mResponseHandler;

    public XAsyncHttpRequest(AbstractHttpClient client, HttpContext contex,
            HttpUriRequest request, XAsyncHttpResponseHandler handler) {
        this.mClient = client;
        this.mContext = contex;
        this.mRequest = request;
        this.mResponseHandler = handler;
    }

    @Override
    public void run() {
        makeRequest();
    }

    /**
     * 发起连接请求
     */
    private void makeRequest() {
        //FIXME:支持重连
        try {
            HttpResponse response = mClient.execute(mRequest, mContext);
            mResponseHandler.sendResponseMessage(response);
            return;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            mResponseHandler.sendFailureMessage(e, "can't resolve host");
            return;
        } catch (SocketException e) {
            e.printStackTrace();
            mResponseHandler.sendFailureMessage(e, "socket exception");
            return;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            mResponseHandler.sendFailureMessage(e, "socket time out");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            mResponseHandler.sendFailureMessage(e, "IOException");
            return;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
