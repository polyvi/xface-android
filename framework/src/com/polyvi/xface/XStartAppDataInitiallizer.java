
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

import com.polyvi.xface.core.XConfiguration;
import com.polyvi.xface.core.XISystemContext;
import com.polyvi.xface.util.XAppUtils;
import com.polyvi.xface.util.XLog;

import android.os.AsyncTask;

public class XStartAppDataInitiallizer extends AsyncTask<Void, Void, Boolean> {
    private final static String CLASS_NAME = XStartAppDataInitiallizer.class
            .getName();
    private Runnable mFinishehCallback;
    private XISystemContext mContext;

    public XStartAppDataInitiallizer(Runnable callback, XISystemContext context) {
        mFinishehCallback = callback;
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        boolean ret = XAppUtils.initAppData(mContext.getContext(),
                XConfiguration.getInstance().getStartAppId(mContext));
        if (!ret) {
            XLog.w(CLASS_NAME, "handle startapp workspace data error.");
        }
        return ret;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mFinishehCallback.run();
        super.onPostExecute(result);
    }

}
