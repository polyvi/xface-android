
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


import android.os.AsyncTask;

/**
 * 该类主要实现启动前的准备工作,如果首次安装就会执行解压这些费时的工作，这样把解压放到另外一个线程中就不会让UI线程阻塞
 */
public class XPrepareWorkEnvronmentTask extends AsyncTask<Void, Void, Void> {

    private XSystemBootstrap mBootstrap;

    private XFaceMainActivity mActivity;

    public XPrepareWorkEnvronmentTask(XSystemBootstrap bootstrap,
            XFaceMainActivity activity) {
        this.mBootstrap = bootstrap;
        this.mActivity = activity;
    }

    /**
     * 执行启动之前的准备工作
     */
    @Override
    protected Void doInBackground(Void... params) {
        mBootstrap.prepareWorkEnvironment();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mBootstrap.boot();
        super.onPostExecute(result);
    }
}
