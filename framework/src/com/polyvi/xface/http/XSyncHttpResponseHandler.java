
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

import org.apache.http.HttpResponse;

import com.polyvi.xface.util.XLog;

public class XSyncHttpResponseHandler extends XAsyncHttpResponseHandler {
    private final static String CLASS_NAME = XSyncHttpResponseHandler.class
            .getSimpleName();
    XSynHttpResponse mHttpResponse;

    public XSyncHttpResponseHandler() {
        mHttpResponse = new XSynHttpResponse(null, null);
    }

    @Override
    public void sendResponseMessage(HttpResponse response) {
        mHttpResponse.setHttpResponse(response);
    };

    @Override
    protected void sendFailureMessage(Throwable e, String body) {
        e.printStackTrace();
        XLog.e(CLASS_NAME, body);
        mHttpResponse.setException(new XSynHttpResponse.NetworkException(e,
                body));
    }

    /**
     * 获取http响应体 如果请求失败 返回null
     * 
     * @return
     */
    public XSynHttpResponse getHttpResponse() {
        return mHttpResponse;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

}
