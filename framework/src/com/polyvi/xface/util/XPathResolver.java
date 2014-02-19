
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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * 把传入的路径解析为对应的相对于app workspace的路径、sdcard路径或其他绝对路径
 */
public class XPathResolver {
    private String mPath;
    private String mAppWorkspace;
    private Context mContext;

    public enum Scheme
    {
        NONE,
        FILE,
        CONTENT,
        HTTP,
        HTTPS,
        UNKNOWN
    }

    private static final String FILE_SCHEME = "file";
    private static final String CONTENT_SCHEME = "content";
    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";

    private static final String SDCARD_HOST = "sdcard";
    private static final String LOCAL_HOST = "localhost";
    private static final String NULL_HOST = "";

    /**
     *
     * @param path          需要解析的路径
     * @param appWorkspace  app的workspace
     */
    public XPathResolver(String path, String appWorkspace) {
        mPath = path;
        mAppWorkspace = appWorkspace;
    }

    /**
     *
     * @param path          需要解析的路径
     * @param appWorkspace  app的workspace
     * @param context       解析Content协议用到的context
     */
    public XPathResolver(String path, String appWorkspace, Context context) {
        mPath = path;
        mAppWorkspace = appWorkspace;
        mContext = context;
    }

    /**
     * 根据host获取跟路径
     *
     * @param host
     *            主机名
     * @return    如果host存在，就返回其对应的跟路径
     */
    private String getRootByHost( String host )
    {
        //目前只有两种host，sdcard host和默认的local host
        if(null == host) {
            return null;
        } else if( host.equals(SDCARD_HOST))
        {
            return getSdcardPath();
        }else if( host.equals(NULL_HOST) || host.equals(LOCAL_HOST))
        {
            return "";
        }
        return null;
    }

    /**解析路径
     *
     * @return 路径合法就返回在当前系统中对应的路径，否则就返回null。
     *         路径不合法可能是因为以下原因之一：
     *         1、file://... uri不符合格式或主机不存在，目前只有sdcard host和默认的local host
     *         2、sdcard不可访问，
     *         3、相对app workspace的路径不在app workspace之内。
     */
    public String resolve()
    {
        //根据协议解析路径
        if(null == mPath) {
            return null;
        }
        Uri uri = Uri.parse(mPath);
        String schemeString = uri.getScheme();
        if(null == schemeString) {//相对app workspace的路径
            return resolveAppRelativePath(mPath);
        } else if (schemeString.equals(FILE_SCHEME)) {//解析文件协议
            String host = uri.getHost();
            String path = uri.getPath();
            return resolveLocalFileUri(host, path);
        } else if (schemeString.equals(CONTENT_SCHEME)) {//解析CONTENT协议
            return resolveContentUri(mPath);
        } else if (schemeString.equals(HTTP_SCHEME) || schemeString.equals(HTTPS_SCHEME)) {//解析http协议
            return mPath;
        }
       return null;
    }

    /**解析路径
    *
    * @return 路径合法就返回在当前系统中对应的路径，否则就返回null。
    *         路径不合法可能是因为以下原因之一：
    *         1、file://... uri不符合格式或主机不存在，目前只有sdcard host和默认的local host
    *         2、sdcard不可访问，
    *         3、相对app workspace的路径不在app workspace之内。
    */
   public Scheme getSchemeType()
   {
       //根据uri解析协议的类型
       Uri uri = Uri.parse(mPath);
       String schemeString = uri.getScheme();
       if(null == schemeString) {//相对app workspace的路径
           return Scheme.NONE;
       } else if (schemeString.equals(FILE_SCHEME)) {//文件协议
           return Scheme.FILE;
       } else if (schemeString.equals(CONTENT_SCHEME)) {//CONTENT协议
           return Scheme.CONTENT;
       } else if (schemeString.equals(HTTP_SCHEME)) {//http协议
           return Scheme.HTTP;
       }else if ( schemeString.equals(HTTPS_SCHEME)) {//https协议
           return Scheme.HTTPS;
       }
       return Scheme.UNKNOWN;
   }

    /**
     * 获得当前系统的sdcard路径
     *
     * @return    如果sdcard可访问，则返回带当前系统的sdcard路径，否则返回null。
     */
     private  String getSdcardPath() {
         String path = null;
         if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
             path = Environment.getExternalStorageDirectory().getAbsolutePath();
         }
         return path;
     }

    /**
     * 解析本地文件uri为绝对路径
     *
     *@param host
     *            uri的host部分
     *@param path
     *            uri的path部分
     * @return   如果主机存在，则返回uri在当前系统对应的文件路径，否则返回null。
     */
    private String resolveLocalFileUri(String host, String path) {
        //根据host获取跟路径
        String root = getRootByHost(host);
        if(null != root) {
            return root + path;
        }
        return null;
    }

    /**
     * 解析content uri为绝对路径
     *
     *@param uri
     *           content uri
     * @return   成功返回文件绝对路径，失败返回null。
     */
    private String resolveContentUri(String uri) {
        if(null != mContext) {
            Cursor cursor = mContext.getContentResolver().query(Uri.parse(mPath), new String[] { MediaStore.Images.Media.DATA }, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            String path = null;
            if(cursor.moveToFirst()) {
                path = cursor.getString(column_index);
            }
            cursor.close();
            return path;
        }
        return null;
    }

    /**
     * 解析相对 app workspace 的路径
     *
     * @param path
     *            要解析的路径
     *
     * @return 路径在app workspace之内，则返回该路径的绝对路径，否则返回null。
     */
    private String resolveAppRelativePath(String path) {
        File file = new File(mAppWorkspace, path);
        String absolutePath = null;
        try {
            absolutePath = file.getCanonicalPath();
            //路径是否在app workspace 之内
            if(!XFileUtils.isFileAncestorOf(mAppWorkspace, absolutePath)) {
                absolutePath = null;
            }
        } catch (IOException e) {
            absolutePath = null;
            e.printStackTrace();
        }
        return absolutePath;
    }

    /**
     * 返回Uri
     * @return
     */
    public Uri getUri()
    {
        switch (getSchemeType()) {
        case NONE:
        case FILE:
        case CONTENT:
            return Uri.parse(XConstant.FILE_SCHEME + resolve());
        default:
            return Uri.parse(mPath);
        }
    }
}

