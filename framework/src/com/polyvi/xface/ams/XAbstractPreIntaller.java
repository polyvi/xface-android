
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

package com.polyvi.xface.ams;

import android.os.AsyncTask;

import com.polyvi.xface.util.XLog;

public abstract class XAbstractPreIntaller extends
        AsyncTask<Void, Void, Boolean> implements XIPreInstallTask {

    private static final String CLASS_NAME = XAbstractPreIntaller.class
            .getSimpleName();
    private XIPreInstallListener mPreInsallListener;

    public XAbstractPreIntaller(XIPreInstallListener listener) {
        mPreInsallListener = listener;
    }

    @Override
    public void run() {
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (params.length > 0 ) {
            XLog.e(CLASS_NAME, "system start params error.");
            return false;
        }
       return install();
    }

    /**
     * 抽象方法 app安装的具体实现
     *
     * @param observer
     * @return
     */
    abstract protected boolean install();

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success) {
            mPreInsallListener.onSuccess();
        } else {
            mPreInsallListener.onFailure();
        }
    }
}
