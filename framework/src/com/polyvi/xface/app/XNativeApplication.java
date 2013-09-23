
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

import java.util.List;

import com.polyvi.xface.XStartParams;
import com.polyvi.xface.util.XAppUtils;
import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XStringUtils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

public class XNativeApplication implements XIApplication {

    private XAppInfo mAppInfo;

    private Context mContext;

    private static final String PARAMS = "params";

    public XNativeApplication(XAppInfo info, Context context) {
        this.mAppInfo = info;
        this.mContext = context;
    }

    @Override
    public boolean start(XStartParams params) {
        String entry = getAppInfo().getEntry();
        if (null == entry) {
            return false;
        }

        PackageInfo pi = null;
        try {
            pi = mContext.getPackageManager().getPackageInfo(entry, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pm = mContext.getPackageManager();
            List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
            if (null == apps || null == apps.iterator().next()) {
                return false;
            }
            ResolveInfo ri = apps.iterator().next();
            if (null == ri) {
                return false;
            }
            String className = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            if (null != params && !XStringUtils.isEmptyString(params.data)) {
                intent.putExtra(PARAMS, params.data);
            }
            ComponentName cn = new ComponentName(entry, className);
            intent.setComponent(cn);
            mContext.startActivity(intent);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public String getAppId() {
        return mAppInfo.getAppId();
    }

    @Override
    public boolean close() {
        // TODO 在NativeApp不能实现AMS去关闭打开的apk程序
        return false;
    }

    @Override
    public String getAppIconUrl() {
        return XConstant.FILE_SCHEME
                + XAppUtils.generateAppIconPath(mAppInfo.getAppId(),
                        mAppInfo.getIcon());
    }

    @Override
    public XAppInfo getAppInfo() {
        return mAppInfo;
    }

    @Override
    public void updateAppInfo(XAppInfo appInfo) {
        mAppInfo = appInfo;
    }


}