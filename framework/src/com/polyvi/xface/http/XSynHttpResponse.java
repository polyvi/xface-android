
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

/**
 * 表示同步http请求的响应结果
 * 
 */
public class XSynHttpResponse {

    private HttpResponse mHttpResponse;
    private NetworkException mException;

    public XSynHttpResponse() {

    }

    public XSynHttpResponse(HttpResponse response, NetworkException exception) {
        mHttpResponse = response;
        mException = exception;
    }

    public void setHttpResponse(HttpResponse response) {
        mHttpResponse = response;
    }

    public HttpResponse getResponse() {
        return mHttpResponse;
    }

    public void setException(NetworkException exception) {
        mException = exception;
    }

    public NetworkException getException() {
        return mException;
    }

    public static class NetworkException extends Exception {
        private static final long serialVersionUID = 1L;
        private Throwable mError;
        private String mMessage;

        public NetworkException(Throwable e, String message) {
            mError = e;
            mMessage = message;
        }

        @Override
        public String getMessage() {
            return mMessage;
        }

        public Throwable getThrowable() {
            return mError;
        }
    }
}
