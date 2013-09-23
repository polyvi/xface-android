
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

package com.polyvi.xface.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class XExternalStorageScanner {
    private static final String CLASS_NAME = XExternalStorageScanner.class.getSimpleName();
    private static final int    LIST_LENGTH = 10;

    /**
     * 获取可用的外部存储卡，没有则返回null
     */
    public static String getExternalStoragePath() {
        List<String> mMounts = new ArrayList<String>(LIST_LENGTH);
        List<String> mVold = new ArrayList<String>(LIST_LENGTH);
        readMounts(mMounts);
        readVold(mVold);
        getIntersectMount(mMounts, mVold);
        mVold.clear();
        getAvailableMount(mMounts);
        return mMounts.isEmpty() ? null : mMounts.get(0);
    }

    /**读取配置文件/proc/mounts 获取文件系统挂载信息
     * 如 [/mnt/sdcard, /mnt/extsdcard]
     */
    private static void readMounts(List<String> mMounts) {
        mMounts.add("/mnt/sdcard");
        try {
            File mountFile = new File("/proc/mounts");
            if (mountFile.exists()) {
                Scanner scanner = new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("/dev/block/vold/")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[1];
                        if (!element.equals("/mnt/sdcard")) {
                            mMounts.add(element);
                        }
                    }
                }
            }
        } catch (IOException e) {
            mMounts.clear();
            XLog.e(CLASS_NAME, e.getMessage());
        }
    }

    /*** 读取配置文件/system/etc/vold.fstab获取设备
     * 如 [/mnt/sdcard, /mnt/extsdcard, /mnt/fat]
     */
    private static void readVold(List<String> mVold) {
        mVold.add("/mnt/sdcard");
        try {
            File voldFile = new File("/system/etc/vold.fstab");
            if (voldFile.exists()) {
                Scanner scanner = new Scanner(voldFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("dev_mount")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[2];
                        if (element.contains(":")) {
                            element = element.substring(0, element.indexOf(":"));
                        }
                        if (!element.equals("/mnt/sdcard")){
                            mVold.add(element);
                        }
                    }
                }
            }
        } catch (IOException e) {
            mVold.clear();
            XLog.e(CLASS_NAME, e.getMessage());
        }
    }

    /**
     * 求mMounts和mVold交集,结果保存在mMounts中
     */
    private static void getIntersectMount(List<String> mMounts,
            List<String> mVold) {
        for (int i = 0; i < mMounts.size(); i++) {
            String mount = mMounts.get(i);
            if (!mVold.contains(mount)){
                mMounts.remove(i--);
            }
        }
    }

    /**
     * 获取可用的挂载点
     * @param map
     * @param mMounts
     */
    private static void getAvailableMount(List<String> mMounts) {
        Iterator<String> it = mMounts.iterator();
        while(it.hasNext()) {
            String mount = it.next();
            File root = new File(mount);
            if (!root.exists() || !root.isDirectory() || !root.canWrite()) {
                it.remove();
            }
        }
    }
}
