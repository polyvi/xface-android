
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.content.Context;

/**
 * zip压缩解压缩的类，以后可以在类中扩展带密码的加压解压的方法。
 * */
public class XZipper {

    private static final String CLASS_NAME = XZipper.class.getSimpleName();

    /**
     * 对目录或文件进行zip压缩
     *
     * @param srcFilePath
     *            待压缩的目录
     * @param zipFileName
     *            要压缩成的zip文件名
     */
    public void zipDir(String srcFilePath, String zipFileName)
            throws NullPointerException, FileNotFoundException, IOException,
            IllegalArgumentException {
        if (XStringUtils.isEmptyString(srcFilePath)
                || XStringUtils.isEmptyString(zipFileName)) {
            throw new IllegalArgumentException();
        }
        File srcFile = new File(srcFilePath);
        if (!srcFile.exists()) {
            throw new FileNotFoundException();
        }
        //当要压缩的是一个文件时候，如果要压缩成的文件名和它同名，则抛出异常
        if (srcFile.isFile() && zipFileName.equals(srcFilePath)) {
            throw new IllegalArgumentException();
        }
        File zipFile = new File(zipFileName);
        File zipFileParent = zipFile.getParentFile();
        if (!zipFileParent.exists()) {
            zipFileParent.mkdirs();
        }
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        String entryPath = "";
        if (srcFile.isDirectory()) {
            entryPath = srcFile.getName() + File.separator;
        }
        compressDir(srcFilePath, zos, entryPath);
        zos.close();
    }

    /**
     * 对多个目录或文件进行zip压缩
     *
     * @param srcFilePaths
     *            待压缩的文件列表
     * @param zipFileName
     *            要压缩成的zip文件名
     */
    public void zipFiles(String[] srcFilePaths, String zipFileName)
            throws NullPointerException, FileNotFoundException, IOException,
            IllegalArgumentException {
        if (XStringUtils.isEmptyString(zipFileName)) {
            throw new IllegalArgumentException();
        }

        for (String path:srcFilePaths) {
            if (XStringUtils.isEmptyString(path)) {
                throw new IllegalArgumentException();
            }
            File srcFile = new File(path);
            if (!srcFile.exists()) {
                throw new FileNotFoundException();
            }
            //当要压缩的是一个文件时候，如果要压缩成的文件名和它同名，则抛出异常
            if (srcFile.isFile() && zipFileName.equals(path)) {
                throw new IllegalArgumentException();
            }
        }

        File zipFile = new File(zipFileName);
        File zipFileParent = zipFile.getParentFile();
        if (!zipFileParent.exists()) {
            zipFileParent.mkdirs();
        }

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        for (String path:srcFilePaths) {
            File entry = new File(path);
            String entryPath = "";
            if (entry.isDirectory()) {
                entryPath = entry.getName() + File.separator;
            }
            compressDir(path, zos, entryPath);
        }
        zos.close();
    }

    /**
     * zip或者压缩一个文件或者文件夹
     *
     * @param srcFilePath
     *            带压缩的文件或文件夹
     * @param zos
     *            zip输出流 {@link ZipOutputStream}
     * @param entryPath
     *            要写入到zip文件中的文件或文件夹的路径
     * @throws IOException
     */
    private void compressDir(String srcFilePath, ZipOutputStream zos,
            String entryPath) throws IOException {
        File zipDir = new File(srcFilePath);
        String[] dirList = zipDir.list();
        if (null == dirList || 0 == dirList.length || zipDir.isFile()) {
            writeToZip(zipDir, zos, entryPath);
        } else {
            for (String pathName : dirList){
                File f = new File(zipDir, pathName);
                if (f.isDirectory()) {
                    String filePath = f.getPath();
                    compressDir(filePath, zos, entryPath + f.getName() + File.separator);
                    continue;
                }
                writeToZip(f, zos, entryPath);
            }
        }
    }

    /**
     * 将文件写入zip文件中
     *
     * @param srcFile
     *            源文件
     * @param zos
     *            zip输出流 {@link ZipOutputStream}
     * @param entryPath
     *            要写入到zip文件中的文件或文件夹的路径
     * */
    private void writeToZip(File srcFile, ZipOutputStream zos,
            String entryPath) throws IOException {

        ZipEntry anEntry = null;

        if (srcFile.isDirectory()) {
            anEntry = new ZipEntry(entryPath);
            zos.putNextEntry(anEntry);
            return;
        }
        anEntry = new ZipEntry(entryPath + srcFile.getName());
        zos.putNextEntry(anEntry);
        FileInputStream fis = new FileInputStream(srcFile);
        byte[] readBuffer = new byte[XConstant.BUFFER_LEN];
        int bytesIn = 0;
        bytesIn = fis.read(readBuffer);
        while (bytesIn != -1) {
            zos.write(readBuffer, 0, bytesIn);
            bytesIn = fis.read(readBuffer);
        }
        fis.close();
        zos.closeEntry();
    }

    /**
     * 解压zip文件
     *
     * @param targetPath
     *            解压的目标路径
     * @param zipFilePath
     *            zip包路径
     */
    public void unzipFile(String targetPath, String zipFilePath)
            throws FileNotFoundException, IOException {
        File zipFile = new File(zipFilePath);

        InputStream is = null;
        is = new FileInputStream(zipFile);
        if(!unzipFileFromStream(targetPath, is)) {
            throw new IOException();
        }
    }

    /**
     * 从assets文件资源中读取zip包进行解压
     *
     * @param targetPath
     *            解压的目标目录路径
     * @param context
     * @param assetFileName
     *            要解压的assets资源文件名
     */
    public boolean unzipFileFromAsset(String targetPath,
            Context context, String assetFileName) throws IOException {
        InputStream is = context.getAssets().open(assetFileName);
        return unzipFileFromStream(targetPath, is);
    }

    /**
     * 从输入流中读取zip文件数据进行解压
     *
     * @param targetPath
     *            解压的目标路径
     * @param is
     *            源zip包输入流
     * @return 解压是否成功
     * @throws IOException
     */
    private boolean unzipFileFromStream(String targetPath, InputStream is)
            throws IOException {
        File dirFile = new File(targetPath + File.separator);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry = null;
        while (null != (entry = zis.getNextEntry())) {
            String zipFileName = entry.getName();
            if (entry.isDirectory()) {
                File zipFolder = new File(targetPath + File.separator
                        + zipFileName);
                if (!zipFolder.exists()) {
                    zipFolder.mkdirs();
                }
            } else {
                File file = new File(targetPath + File.separator + zipFileName);

                // 如果要解压的文件目标位置父目录不存在，创建对应目录
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    XLog.d(CLASS_NAME,
                            "Can't write file: " + file.getAbsolutePath());
                    e.printStackTrace();
                    return false;
                }
                int readLen = 0;
                byte buffer[] = new byte[XConstant.BUFFER_LEN];
                while (-1 != (readLen = zis.read(buffer))) {
                    fos.write(buffer, 0, readLen);
                }
                fos.close();
            }
        }
        zis.close();
        is.close();
        return true;
    }
}
