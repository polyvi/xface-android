
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

package com.polyvi.xface.exceptionReporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.polyvi.xface.core.XConfiguration;
import com.polyvi.xface.util.XFileUtils;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XStringUtils;

/**
 * 该类用于具体处理crashInfo,包括保存crashInfo到sd卡和发送到服务端
 *
 * @author Administrator
 *
 */
public class XExceptionReportHandler {
    private static final String CLASS_NAME = XExceptionReportHandler.class
            .getSimpleName();
    /** 异常信息的文件 */
    private static final String CRASH_REPORT_FILE_PATH = "crash/";
    private static final String CRASH_REPORT_FILE = ".json";
    private static final String DATE_FORMAT_TYPE = "yyyy-MM-dd_HH-mm-ss";
    /** crashInfo保存的路径 */
    private String mFileDir;
    public XExceptionReportHandler() {
        mFileDir = createCrashReportDir();
    }

    /**
     * 保存crashReport到sd卡
     *
     * @param crashInfo
     *            :崩溃信息类
     */
    public void saveCrashReportLocal(XCrashInfo crashInfo) {
        if (null != crashInfo && null != mFileDir) {
            String filePath = new File(mFileDir, generateReportFileNameByTime())
                    .getAbsolutePath();
            String crashString = crashInfo.toString();
            String wrapString = crashString.replaceAll("\\\\n", "\n");
            String IndentationString = wrapString.replaceAll("\\\\t", "\t");
            XFileUtils.writeFileByString(filePath, IndentationString);
        }
    }

    /**
     * 在sd卡下创建crash文件保存路径
     *
     * @return null:创建不成功，String：crash文件保存路径
     */
    private String createCrashReportDir() {
        String workDir = XConfiguration.getInstance().getWorkDirectory();
        if (XStringUtils.isEmptyString(workDir)) {
            XLog.e(CLASS_NAME, "Failed to get workDir at:" + workDir);
            return null;
        }
        File saveDir = new File(workDir, CRASH_REPORT_FILE_PATH);
        String saveDirPath = saveDir.getAbsolutePath();
        if (saveDir.exists() || saveDir.mkdirs()) {
            return saveDirPath;
        }
        XLog.e(CLASS_NAME, "Failed to create crash log directory at:"
                + saveDirPath);
        return null;
    }

    /**
     * 根据文件创立的时间来命名文件名.
     *
     * @return 默认文件的名字
     */
    private String generateReportFileNameByTime() {
        return new SimpleDateFormat(DATE_FORMAT_TYPE).format(Calendar
                .getInstance().getTime()) + CRASH_REPORT_FILE;
    }
}
