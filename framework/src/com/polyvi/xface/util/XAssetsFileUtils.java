
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
import java.io.InputStream;

import android.content.Context;

public class XAssetsFileUtils {
    private static final String CLASS_NAME = XAssetsFileUtils.class
            .getSimpleName();

    /**
     * 判断路径是否为目录
     *
     * @param context
     * @param filePath
     * @return
     * @throws IOException
     */
    public static boolean isDirectory(Context context, String filePath){
        try {
            context.getAssets().open(filePath);
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    /**
     * 判断路径是否为文件
     *
     * @param context
     * @param filePath
     * @return
     * @throws IOException
     */
    public static boolean isFile(Context context, String filePath)
            throws IOException {
        try {
            context.getAssets().open(filePath);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 递归的遍历assets目录
     *
     * @param context
     *            上下文环境
     * @param srcDir
     *            需要遍历的目录路径
     * @param visitor
     *            文件处理器
     */
    public static void walkAssetsDirectory(Context context, String srcDir,
            XFileVisitor visitor) {
        try {
            if (!isDirectory(context, srcDir) || !visitor.isContinueTraverse()) {
                return;
            }
            String childrens[] = context.getAssets().list(srcDir);
            for (int index = 0; index < childrens.length; index++) {
                if (isFile(context, srcDir + File.separator + childrens[index])) {
                    visitor.visit(srcDir + File.separator + childrens[index]);
                } else {
                    walkAssetsDirectory(context, srcDir + File.separator
                            + childrens[index], visitor);
                }
            }
        } catch (IOException e) {
            XLog.e(CLASS_NAME, e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    /**
	 * @param context
	 *            系统上下文
	 * @param fileName
	 *            源文件名或者源目录名
	 * @param desPath
	 *            需要拷贝的目标路径
	 * @return 成功 true 失败 false
	 */
	public static boolean copyAssetsToTarget(Context context,
			String srcFileName, String desPath) {
		try {
			String childrens[] = context.getAssets().list(srcFileName);
			if (childrens.length > 0) {
				File desFile = new File(desPath);
				if (!desFile.exists() && !desFile.mkdirs()) {
					throw new IOException();
				}
				int len = childrens.length;
				for (int i = 0; i < len; i++) {
					copyAssetsToTarget(context, srcFileName + "/"
							+ childrens[i],
							new File(desPath, childrens[i]).getAbsolutePath());
				}
			} else {
				String targetFilePath = new File(desPath).getAbsolutePath();
				InputStream is = context.getAssets().open(srcFileName);
				XFileUtils.createFileByData(targetFilePath, is);
				is.close();
			}

		} catch (IOException e) {
			XLog.e(CLASS_NAME, e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
