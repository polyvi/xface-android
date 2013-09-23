
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.polyvi.xface.util.XConstant;
import com.polyvi.xface.util.XLog;
import com.polyvi.xface.util.XUtils;

public class XOnlineResourceIterator implements Iterator<char[]>{
    private static final String CLASS_NAME = XOnlineResourceIterator.class.getName();
    private XIResourceFilter mFilter;
    private Cursor mCursor;
    private int mCursorCount;
    private SQLiteDatabase mDatabase;
    private InputStream mNextInputStream;

    public XOnlineResourceIterator(String appUrl, XIResourceFilter filter) {
        mFilter = filter;
        mCursorCount = 0;
        initDatabase(appUrl);
        mNextInputStream = traverseNext();
    }

    /**
     * 初始化数据库
     * @param appUrl[in]
     */
    private void initDatabase(String appUrl) {
        String appCachePath = XConfiguration.getInstance().getSysDataDir()
                    + XConstant.APP_CACHE_PATH + File.separator + XOnlineMode.OFFLINE_DATABASE_NAME;
        File file = new File(appCachePath);
        if( file.exists() ) {
            mDatabase = SQLiteDatabase.openOrCreateDatabase(appCachePath, null);
            /**CacheEntries表结构实例
             * |RecNo  |cache  |type  |resource|
             * |      1|      1|     4|       1|
             * |      2|      1|     4|       2|
             * |      3|      1|     2|       3|
             * |      4|      1|     5|       4|
             * |      5|      1|     4|       5|
             */
            String sqlCommand = "select * from CacheResources where id in ( select resource from " +
                                "CacheEntries where cache in( select cache from CacheEntries where " +
                                "resource in ( select id from CacheResources where url = \'" + appUrl + "\')))";
            mCursor = mDatabase.rawQuery(sqlCommand, null);
            if( !mCursor.moveToFirst() ) {
                XLog.e(CLASS_NAME, "Database moveToFirst failed!");
                mDatabase.close();
                mDatabase = null;
                mCursor.close();
                return;
            }
            mCursorCount = mCursor.getCount();
        }
    }

    @Override
    public boolean hasNext() {
        return mNextInputStream != null;
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
        InputStream is = null;
        while( mCursorCount-- > 0 ) {
            String fileUrl = null;
            int fileIndex = 0;
            fileIndex = mCursor.getColumnIndex("url");
            if (-1 != fileIndex){
                fileUrl = mCursor.getString(fileIndex);
            }
            if (null == fileUrl) {
                mDatabase.close();
                mDatabase = null;
                mCursor.close();
                return null;
            }

            if (mFilter.accept(fileUrl)) {
                is = readInputStreamFromAppCache();
                if(null == is) {
                    break;
                }
                mCursor.moveToNext();
                break;
            }
            mCursor.moveToNext();
        }
        if(mCursorCount <= 0 && null != mDatabase){
            mDatabase.close();
            mDatabase = null;
            mCursor.close();
        }
        return is;
    }

    /**
     * 从缓存中读取文件的输入流
     *
     * @return 读取的输入流
     */
    private InputStream readInputStreamFromAppCache(){
        InputStream is = null;
        int columIndex = mCursor.getColumnIndex("data");
        if(-1 == columIndex) {
            XLog.e(CLASS_NAME, "data doesn't exist in Database!");
            mDatabase.close();
            mDatabase = null;
            mCursor.close();
            return is;
        }
        int dataIndex = mCursor.getInt(columIndex);
        String sqlCommand = "select * from CacheResourceData where id = \'" + dataIndex + "\'";
        Cursor cursor = mDatabase.rawQuery(sqlCommand, null);
        if( !cursor.moveToFirst() ) {
            XLog.e(CLASS_NAME, "Database moveToFirst failed!");
            mDatabase.close();
            mDatabase = null;
            mCursor.close();
            cursor.close();
            return is;
        }
        int fileIndex = cursor.getColumnIndex("data");
        byte[] fileData = null;
        if(-1 != fileIndex){
            fileData = cursor.getBlob(fileIndex);
        }
        if( null != fileData ) {
            is = new ByteArrayInputStream(fileData);
        }
        cursor.close();
        return is;
    }

    @Override
    public void remove() {
        //TODO: 不支持remove的操作,抛出异常
    }

}
