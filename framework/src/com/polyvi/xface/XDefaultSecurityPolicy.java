
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

package com.polyvi.xface;

import com.polyvi.xface.app.XApplication;
import com.polyvi.xface.core.XAppCheckListener;
import com.polyvi.xface.core.XISystemContext;


public class XDefaultSecurityPolicy implements XSecurityPolicy {

    private XISystemContext mCtx;
    public XDefaultSecurityPolicy(XISystemContext ctx) {
        mCtx = ctx;
    }

    @Override
    public boolean checkAppStart(XApplication app, XAppCheckListener listener) {
        listener.onCheckSuccess(app, mCtx);
        return true;
    }

    @Override
    public boolean checkAppClose(XApplication app) {
        return true;
    }

}
