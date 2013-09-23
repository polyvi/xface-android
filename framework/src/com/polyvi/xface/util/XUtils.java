
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;



import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * 提供一些工具方法
 */
public class XUtils {
    private static final String CLASS_NAME = XUtils.class.getSimpleName();
    private static int requstCode = 1; // 用于产生startActivity用到的requst code

    /**
     * 生成一个随机的int类型id
     */
    public static int generateRandomId() {
        Random r = new Random();
        return r.nextInt();
    }

    /**
     * 构建查找的select语句
     *
     * @param projections
     *            要查找的字段项
     * @param projectionsValue
     *            字段项所对应的值
     * @return select语句
     */
    public static StringBuilder constructSelectionStatement(
            ArrayList<String> projections, ArrayList<String> projectionsValue) {
        StringBuilder selection = null;
        Iterator<String> projectionsIter = projections.iterator();
        Iterator<String> projectionsValueIter = projectionsValue.iterator();
        while (projectionsValueIter.hasNext() && projectionsIter.hasNext()) {
            String projection = projectionsIter.next();
            String projectionValue = projectionsValueIter.next();
            if (0 != projectionValue.length()) {
                if (null == selection) {
                    selection = new StringBuilder();
                } else {
                    selection.append(" AND ");
                }
                // TODO:这里没有考虑一些特殊情况
                String matchValue = projectionValue.replace("*", "%");
                selection.append(projection);
                selection.append(" LIKE '");
                selection.append(matchValue);
                selection.append("'");
            }
        }
        return selection;
    }

    public static int genActivityRequestCode() {
        return requstCode++;
    }

    /**
     * 通过输入流获取二进制数组
     *
     * @param is
     *            [in] 输入流
     *
     * @return 返回二进制数组
     */
    public static byte[] readBytesFromInputStream(InputStream is) {
        if (null == is) {
            return null;
        }
        int dataLen = XConstant.BUFFER_LEN;
        int len = 0;
        byte[] bytesData = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bytesData = new byte[dataLen];
            while ((len = is.read(bytesData, 0, dataLen)) != -1) {
                baos.write(bytesData, 0, len);
            }
            baos.flush();
            bytesData = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            XLog.d(CLASS_NAME, "get bytes from InputStream failed!");
            e.printStackTrace();
            return null;
        }
        return bytesData;
    }

    /**
     * 通过输入流获取字符数组
     *
     * @param inputStream
     *            [in] 输入流
     *
     * @return 字符数组
     * */
    public static char[] readCharArrayFromInputStream(InputStream inputStream) {
        if (null == inputStream) {
            return null;
        }
        char[] charArray = null;
        try {
            int bufferLen = inputStream.available();
            charArray = new char[bufferLen];
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            bufferedReader.read(charArray, 0, bufferLen);
            inputStream.close();
        } catch (UnsupportedEncodingException e) {
            XLog.e(CLASS_NAME, "Convert InputStream to char array error!");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            XLog.e(CLASS_NAME, "Convert InputStream to char array error!");
            e.printStackTrace();
            return null;
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            XLog.e(CLASS_NAME, "InputStream close error!");
            e.printStackTrace();
            return null;
        }
        return charArray;
    }

    /**
     * 用于判断设备是否是pad
     * @param context
     * @return true 是pad
     *         false 是phone
     */
    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    /**
     * 解析指定路径的图片成bitmap格式
     * 注意：解析没有用decodeFile/decodeStream是因为这两个函数解析大图片的时候
     * 会导致ava.lang.OutOfMemoryError: bitmap size exceeds VM budget
     * @param imagePath：图片路径
     * @return 解析得到的Bitmap，如果解析不成功会返回null
     */
    public static Bitmap decodeBitmap(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**设置禁止抖动图片*/
        options.inDither = false;
        /**true：生成的bitmap会分配其像素以便当系统需要回收内存的时候可以被清除*/
        options.inPurgeable = true;
        /**如果inPurgeable为false，本参数会被忽略，true:引用输入数据，false:对输入数据进行深拷贝*/
        options.inInputShareable = true;
        /**解析图片用到的容量，本处16k*/
        options.inTempStorage = new byte[16 * 1024];
        File file = new File(imagePath);
        FileInputStream fs = null;
        Bitmap bitmap = null;
        try {
            fs = new FileInputStream(file);
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
            }
        } catch (FileNotFoundException e) {
            XLog.e(CLASS_NAME, e.getMessage());
        } catch (IOException e) {
            XLog.e(CLASS_NAME, e.getMessage());
        } catch (OutOfMemoryError e) {
            XLog.e(CLASS_NAME, e.getMessage());
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    XLog.e(CLASS_NAME, e.getMessage());
                }
            }
        }
        return bitmap;
    }
}
