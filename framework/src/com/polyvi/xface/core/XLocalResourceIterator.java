
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

package com.polyvi.xface.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XUtils;

/**
 * 本地资源迭代器 仅仅迭代应用中可进行md5校验的文件
 *
 * @param <E>
 *
 */
public class XLocalResourceIterator implements Iterator<char[]> {

    private static final String CLASS_NAME = XLocalResourceIterator.class.getName();
    private XIResourceFilter mFilter;
    private Stack<Enumeration<File>> mDirEnumStack;
    private Enumeration<File> mCurrentDirEnum;
    private InputStream mNextInputStream;

    public XLocalResourceIterator(String appDir, XIResourceFilter filter) {
        if(null == appDir) {
            throw new IllegalArgumentException("XLocalResourceIterator requires valid appDir");
        }
        File file = new File(appDir);
        if(file.exists()) {
            mDirEnumStack = new Stack<Enumeration<File>>();
            mCurrentDirEnum = getDirectoryEntry(new File(appDir));
            mFilter = filter;
            mNextInputStream = traverseNext();
        }
    }

    @Override
    public boolean hasNext() {
        return mNextInputStream != null;
    }

    /**
     * 获得目录列表(包括文件和子目录)
     *
     * @param dir
     *            被扫描的目录
     * @return
     */
    private Enumeration<File> getDirectoryEntry(File dir) {
        Vector<File> files = new Vector<File>();
        File[] fileInDir = dir.listFiles();
        for (int i = 0; i < fileInDir.length; i++) {
            files.addElement(fileInDir[i]);
        }
        return files.elements();
    }

    @Override
    public char[] next() {
        InputStream current = mNextInputStream;
        mNextInputStream = traverseNext();
        char[] content = XUtils.readCharArrayFromInputStream(current);
        return content;
    }

    /**
     * 获得下一个文件
     *
     * @return
     */
    private InputStream traverseNext() {
        InputStream nextInputStream = null;
        FileInputStream fileInputStream = null;
        while (null == nextInputStream) {
            if (null != mCurrentDirEnum && mCurrentDirEnum.hasMoreElements()) {
                File temp = mCurrentDirEnum.nextElement();
                if (temp.isDirectory()) {
                    Enumeration<File> dirEnum = getDirectoryEntry(temp);
                    mDirEnumStack.push(mCurrentDirEnum);
                    mCurrentDirEnum = dirEnum;
                } else {
                    String absPath = temp.getAbsolutePath();
                    if (mFilter.accept(absPath)) {
                        // 读取输入流 赋值个nextIn
                        try {
                            fileInputStream = new FileInputStream(absPath);
                            nextInputStream = fileInputStream;
                        } catch (FileNotFoundException e) {
                            XLog.e(CLASS_NAME, "File not found!");
                            e.printStackTrace();
                            nextInputStream = null;
                        }
                    }
                }
            } else {
                if (null == mDirEnumStack) {
                    break;
                }
                if (mDirEnumStack.empty()) {
                    break;
                } else {
                    mCurrentDirEnum = mDirEnumStack.pop();
                }
            }
        }
        return nextInputStream;
    }

    @Override
    public void remove() {
    }
}
