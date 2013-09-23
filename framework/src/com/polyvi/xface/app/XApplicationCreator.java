
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

package com.polyvi.xface.app;

import com.polyvi.xface.core.XISystemContext;

public class XApplicationCreator {

    private final static String XFACE_APP_TYPE1 = "xapp";

    private final static String XFACE_APP_TYPE2 = "app";

    public final static String NATIVE_APP_TYPE = "napp";

    private XISystemContext mCtx;

    public XApplicationCreator(XISystemContext ctx) {
        mCtx = ctx;
    }

    public XIApplication create(XAppInfo info) {
        if (info.getType().equals(XFACE_APP_TYPE1)
                || info.getType().equals(XFACE_APP_TYPE2)) {
            XApplication app = new XApplication(info);
            app.init(mCtx);
            return app;
        } else if (info.getType().equals(NATIVE_APP_TYPE)) {
            XNativeApplication app = new XNativeApplication(info,
                    mCtx.getContext());
            return app;
        }
        return null;
    }

    /**
     * 将XIApplication转换成XNativeApplication
     *
     * @param app
     *            XIApplication类型
     * @return XNativeApplication类型
     */
    public static XNativeApplication toNativeApp(XIApplication app) {
        return app instanceof XNativeApplication ? (XNativeApplication) app
                : null;
    }

    /**
     * 将XIApplication转换成XApplication
     *
     * @param app
     *            XIApplication类型
     * @return XApplication类型
     */
    public static XApplication toWebApp(XIApplication app) {
        return app instanceof XApplication ? (XApplication) app : null;
    }
}
