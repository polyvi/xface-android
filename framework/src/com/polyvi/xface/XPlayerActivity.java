
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

import java.io.File;

import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.polyvi.xface.core.XConfiguration;
import com.polyvi.xface.util.XFileUtils;
import com.polyvi.xface.util.XLog;

public class XPlayerActivity extends XFaceMainActivity {
    private static final String CLASS_NAME = XPlayerActivity.class.getName();
    private static final String XFACE_PLAYER_DIR_NAME = "xFacePlayer";

    private final static int TEXT_SIZE = 15;

    private final static double SCALE_X = 0.7;

    private final static double SCALE_Y = 0.45;

    // FIXME:可以考虑用一个工厂创建Bootstrap，不需要一个新的Activity
    @Override
    protected XSystemBootstrap createSystemBootstrap() {
        return new XPlayerSystemBootstrap(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 强制关闭程序进程，目前存在activity退出后进程仍然存在的问题，造成应用开发人员替换了js文件之后，
        // 再启动引擎仍然使用的老的js文件数据，故此处强制退出进程
        System.exit(0);
    }

    @Override
    protected String getWorkDirName() {
        String baseDir = XFileUtils.getSdcardPath();
        if (null == baseDir) {
            return null;
        }
        // 针对player工程直接返回xFacePlayer
        // 返回前会检查目录是否存在，不存在则创建，防止程序崩溃
        String workDirName = baseDir + File.separator + XFACE_PLAYER_DIR_NAME
                + File.separator;
        File workDir = new File(workDirName);
        return workDir.exists() || workDir.mkdir() ? workDirName : null;
    }

    /**
     * 获得手机加密密钥，player不对config文件进行加密这里返回null
     *
     * @return
     */
    protected String getKey() {
        return null;
    }


    /**
     * 生成显示版本号的textView和布局
     *
     */
    private void createVersionTextView() {
        mVersionText = new TextView(this);
        mVersionParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mVersionParams.leftMargin = (int) (dm.widthPixels * SCALE_X);
        mVersionParams.topMargin = (int) (dm.heightPixels * SCALE_Y);
        String versionName = XConfiguration.getInstance().readEngineVersion();
        String buildName = XConfiguration.getInstance().readEngineBuild();
        if(null == versionName || null == buildName) {
            XLog.w(CLASS_NAME, "Please config engine version and build in config.xml!");
            return;
        }
        mVersionText.setTextColor(Color.WHITE);
        mVersionText.setText(versionName + "." + buildName);
        mVersionText.setTextSize(TEXT_SIZE);
    }
}
