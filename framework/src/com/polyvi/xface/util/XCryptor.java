
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.polyvi.xface.exception.XCryptionException;

//该类文件的加密和解密
public class XCryptor {
    private static final String CLASS_NAME = XCryptor.class.getSimpleName();
    // 加密算法名称
    private static final String RSA_ALGORITHM = "RSA";
    // 处理解密数组的片段长度
    private static final int DECRYPT_PART_LENGTH = 128;
    // 处理加密数组的片段长度
    private static final int ENCRYPT_PART_LENGTH = 100;
    // AES加密算法
    private static final String AES_ALGORITHM = "AES";
    //DES加密算法
    private static final String DES_ALGORITHM = "DES";
    //3DES加密算法
    private static final String TRIPLE_DES_ALGORITHM = "DESede/CBC/PKCS5Padding";
    private static final String KEY_ALOGRITHEM = "DESede";
    // 生成的密钥的长度
    private static final int KEY_SIZE = 128;

    /**加解密报错提示*/
    private static final String KEY_EMPTY_ERROR  = "Error:key null or empty";
    private static final String DATA_EMPTY_ERROR = "Error:data null or empty";
    private static final String CONVERTION_ERROR = "Error: Preparing Convertion";
    private static final String CRYPTION_ERROR = "Error:cryption error";
    private static final String OUT_OF_MEMORY_ERROR = "Error:out of memory error";

    /**
     * md5算法
     *
     * @param contentStr
     *            [in] 需要求出md5值的内容
     *
     * @return 返回md5值
     */
    public String calMD5Value(char[] content) {
        //TODO:content是XBASE64后的字符串
        if (null == content) {
            return null;
        }
        MessageDigest md5 = null;
        byte[] byteArray = null;

        try {
            byteArray = String.valueOf(content).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            XLog.e(CLASS_NAME, "String convert to byte array error!");
            e1.printStackTrace();
            return null;
        }

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            XLog.e(CLASS_NAME, "Can't get instance of MD5!");
            e.printStackTrace();
            return null;
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 实例化Cipher
     *
     * @param key
     *            [in] 密钥
     *
     * @return 返回实例化的Cipher
     */
    private Cipher instanceOfCipher(Key key) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException e) {
            XLog.e(CLASS_NAME, "No RSA Algorithm!");
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            XLog.e(CLASS_NAME, "Cipher init failed!");
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            XLog.e(CLASS_NAME, "key is a invalid key!");
            e.printStackTrace();
            return null;
        }
        return cipher;
    }

    /**
     * 通过公钥加密
     * @param data
     * @param publicKey
     * @return
     * @throws XCryptionException
     */
    public byte[] encryptRSA(byte[] data, byte[] publicKey)
            throws XCryptionException, IllegalArgumentException {
        if(null == data || null == publicKey) {
            XLog.e(CLASS_NAME, "encryptRSA param is null!");
            throw new IllegalArgumentException();
        }
        byte[] encryptData = null;
        try {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            RSAPublicKey pubKey = (RSAPublicKey)keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            if(null == cipher) {
                throw new XCryptionException(CRYPTION_ERROR);
            }
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            ByteBuffer rawBuffer = ByteBuffer.wrap(data);
            ByteBuffer encryptBuffer = ByteBuffer.allocate(data.length * 4);
            // 由于cipher.doFinal方法对加密的数组长度有要求，不能超过100位，故采取分段解密的方法
            byte[] encryptPart = new byte[ENCRYPT_PART_LENGTH];
            byte[] doFinalBytes = null;
            int encryptDataLen = 0;
            int encryptPartLen = 0;
            for (int i = 0; i < data.length; i += ENCRYPT_PART_LENGTH) {
                if ((i + ENCRYPT_PART_LENGTH) <= data.length) {
                    encryptPartLen = ENCRYPT_PART_LENGTH;
                } else {
                    encryptPartLen = data.length - i;
                    encryptPart = new byte[encryptPartLen];
                }
                rawBuffer.get(encryptPart, 0, encryptPartLen);
                doFinalBytes = cipher.doFinal(encryptPart);
                encryptDataLen += doFinalBytes.length;
                encryptBuffer.put(doFinalBytes, 0, doFinalBytes.length);
            }
            encryptData = new byte[encryptDataLen];
            encryptBuffer.position(0);
            encryptBuffer.get(encryptData, 0, encryptDataLen);
        } catch (NoSuchAlgorithmException e) {
            XLog.e(CLASS_NAME, "encryptRSA : NoSuchAlgorithmException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        } catch (InvalidKeySpecException e) {
            XLog.e(CLASS_NAME, "encryptRSA : InvalidKeySpecException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        } catch (InvalidKeyException e) {
            XLog.e(CLASS_NAME, "encryptRSA : InvalidKeyException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        } catch (IllegalBlockSizeException e) {
            XLog.e(CLASS_NAME, "encryptRSA : IllegalBlockSizeException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        } catch (BadPaddingException e) {
            XLog.e(CLASS_NAME, "encryptRSA : BadPaddingException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        } catch (NoSuchPaddingException e) {
            XLog.e(CLASS_NAME, "encryptRSA : NoSuchPaddingException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        }
        return encryptData;
    }

    /**
     * 通过私钥加密
     * @param data
     * @param privateKey
     * @return
     * @throws XCryptionException
     */
    public byte[] decryptRSA(byte[] data, byte[] privateKey)
            throws XCryptionException, IllegalArgumentException {
        if(null == data || null == privateKey) {
            XLog.e(CLASS_NAME, "decryptRAS param is null!");
            throw new IllegalArgumentException();
        }
        byte[] decryptData = null;
        try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            RSAPrivateKey priKey = (RSAPrivateKey)keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = instanceOfCipher(priKey);
            if(null == cipher) {
                throw new XCryptionException(CRYPTION_ERROR);
            }
            cipher.init(Cipher.ENCRYPT_MODE, priKey);
            ByteBuffer encryptBuffer = ByteBuffer.wrap(data);
            ByteBuffer decryptBuffer = ByteBuffer.allocate(data.length * 2);
            byte[] encryptPart = new byte[DECRYPT_PART_LENGTH];
            byte[] doFinalBytes = null;
            int decryptDataLen = 0;
            int decryptPartLen = 0;
            for (int i = 0; i < data.length; i += DECRYPT_PART_LENGTH) {
                if ((i + DECRYPT_PART_LENGTH) <= data.length) {
                    decryptPartLen = DECRYPT_PART_LENGTH;
                } else {
                    decryptPartLen = data.length - i;
                    encryptPart = new byte[decryptPartLen];
                }
                encryptBuffer.get(encryptPart, 0, decryptPartLen);
                doFinalBytes = cipher.doFinal(encryptPart);
                decryptDataLen += doFinalBytes.length;
                decryptBuffer.put(doFinalBytes, 0, doFinalBytes.length);
            }
            // 由于android平台doFinal方法问题，需要对生成出来的数组进行特殊处理
            decryptData = handleDoFinalBytes(decryptBuffer, decryptDataLen);
        } catch (NoSuchAlgorithmException e) {
            XLog.e(CLASS_NAME, "decryptRAS : NoSuchAlgorithmException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        } catch (InvalidKeySpecException e) {
            XLog.e(CLASS_NAME, "decryptRAS : InvalidKeySpecException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        } catch (InvalidKeyException e) {
            XLog.e(CLASS_NAME, "decryptRAS : InvalidKeyException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        } catch (IllegalBlockSizeException e) {
            XLog.e(CLASS_NAME, "decryptRAS : IllegalBlockSizeException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        } catch (BadPaddingException e) {
            XLog.e(CLASS_NAME, "decryptRAS : BadPaddingException!");
            e.printStackTrace();
            throw new XCryptionException(CRYPTION_ERROR);
        }
        return decryptData;
    }

    /**
     * 通过公钥解密
     *
     * @param encryptData
     *            [in] 加密数据
     * @param publicKey
     *            [in] 解密公钥
     *
     * @return 返回解密后的数据输入流
     */
    public InputStream decryptByPublicKey(byte[] encryptData,
            RSAPublicKey publicKey) {
        Cipher cipher = instanceOfCipher(publicKey);
        if (null == encryptData || null == publicKey || null == cipher) {
            return null;
        }
        ByteBuffer encryptBuffer = ByteBuffer.wrap(encryptData);
        ByteBuffer decryptBuffer = ByteBuffer.allocate(encryptData.length * 2);
        // 由于cipher.doFinal方法对解密的数组长度有要求，不能超过128位，故采取分段解密的方法
        byte[] decryptPart = new byte[DECRYPT_PART_LENGTH];
        byte[] doFinalBytes = null;
        // 记录解密后的数组长度
        int decryptDataLen = 0;
        int decryptPartLen = 0;
        for (int i = 0; i < encryptData.length; i += DECRYPT_PART_LENGTH) {
            try {
                if ((i + DECRYPT_PART_LENGTH) <= encryptData.length) {
                    decryptPartLen = DECRYPT_PART_LENGTH;
                } else {
                    decryptPartLen = encryptData.length - i;
                    decryptPart = new byte[decryptPartLen];
                }
                encryptBuffer.get(decryptPart, 0, decryptPartLen);
                doFinalBytes = cipher.doFinal(decryptPart);
                decryptDataLen += doFinalBytes.length;
                decryptBuffer.put(doFinalBytes, 0, doFinalBytes.length);
            } catch (IllegalBlockSizeException e) {
                XLog.e(CLASS_NAME, "Data of decryption is over blockSize!");
                e.printStackTrace();
                return null;
            } catch (BadPaddingException e) {
                XLog.e(CLASS_NAME, "Decrypt data failed!");
                e.printStackTrace();
                return null;
            }
        }
        // 由于android平台doFinal方法问题，需要对生成出来的数组进行特殊处理
        byte[] decryptDataBytes = handleDoFinalBytes(decryptBuffer,
                decryptDataLen);
        return new ByteArrayInputStream(decryptDataBytes);
    }

    /**
     * 由于android平台的doFinal方法解密出的二进制数组 会多出-1和0和1的无效数字，平台自身错误，在java平台上不会遇到这种情况。
     * 需要处理掉-1和0和1
     *
     * TODO:以后android的doFinal方法解决此问题后该函数可以删除
     *
     * @param decryptBuffer
     *            [in] 需要处理的二进制缓存
     * @param decryptDataLen
     *            [in] 需要处理的二进制数组长度
     *
     * @return 去掉-1、0、1的二进制数组
     */
    private byte[] handleDoFinalBytes(ByteBuffer decryptBuffer,
            int decryptDataLen) {
        byte decryptData = 0;
        int handledArrayLen = 0;
        for (int i = 0; i < decryptDataLen; i++) {
            decryptData = decryptBuffer.get(i);
            if (decryptData != 0 && decryptData != -1 && decryptData != 1) {
                decryptBuffer.put(decryptData);
                handledArrayLen++;
            }
        }
        byte[] handledArray = new byte[handledArrayLen];
        decryptBuffer.position(decryptDataLen);
        decryptBuffer.get(handledArray, 0, handledArrayLen);
        return handledArray;
    }

    /**
     * 初始化密钥
     *
     * @param key
     *            密钥
     * @return 根据key生成的密钥
     * @throws NoSuchAlgorithmException
     */
    // FIXME:AES的算法应该放到security中，以后添加到扩展中
    private SecretKeySpec initKeyForAES(String key)
            throws NoSuchAlgorithmException {
        if (null == key || key.length() == 0) {
            throw new NullPointerException("key can't be null");
        }
        SecretKeySpec secretKeySpec = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(KEY_SIZE, new SecureRandom(key.getBytes()));
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            secretKeySpec = new SecretKeySpec(enCodeFormat, AES_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException();
        }
        return secretKeySpec;
    }

    /**
     * 用AES算法对字符串加密
     *
     * @param content
     *            需要加密的字符串的内容
     * @param key
     *            密钥
     * @return 加密过后得到byte数组
     */
    public byte[] encryptBytesForAES(byte[] byteContent, String key) {
        try {
            SecretKeySpec secretKeySpec = initKeyForAES(key);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            XLog.d(CLASS_NAME, e.getMessage());
        }
        return null;
    }

    /**
     * 通过AES算法对文件加密
     *
     * @param filePath
     *            文件的绝对路径
     * @param key
     *            密钥
     * @return 成功返回true，失败返回false
     */
    public Boolean encryptFileForAES(String filePath, String key) {
        byte[] contentBytes = XFileUtils.readBytesFromFile(filePath);
        if (null == contentBytes) {
            return false;
        }
        byte[] encryptContent = encryptBytesForAES(contentBytes, key);
        if (null == encryptContent) {
            return false;
        }
        return XFileUtils.writeFileByByte(filePath, encryptContent);
    }

    /**
     * 对byte数组进行解密
     *
     * @param content
     *            经过加密的byte数组的内容
     * @param key
     *            密钥
     * @return 解密过后得到的字符串
     */
    public byte[] decryptBytesForAES(byte[] content, String key) {
        try {
            SecretKeySpec secretKeySpec = initKeyForAES(key);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 解密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对文件进行AES解密
     *
     * @param filePath
     *            文件的绝对路径
     * @param key
     *            密钥
     * @return 成功返回true，失败返回false
     */
    public boolean decryptFileForAES(String filePath, String key) {
        if (null == key) {
            return false;
        }
        byte[] encryptContent = XFileUtils.readBytesFromFile(filePath);
        byte[] fileContent = decryptBytesForAES(encryptContent, key);
        if (null == encryptContent || null == fileContent) {
            return false;
        }
        return XFileUtils.writeFileByString(filePath, new String(fileContent));
    }


    /**
     * 用DES算法对字符串加密
     *
     * @param content
     *            需要加密的字符串的内容
     * @param key
     *            密钥
     * @return 加密过后得到byte数组
     * @throws XCryptionException
     */
    public  byte[] encryptBytesForDES(byte[] content, byte[] key)
            throws XCryptionException {
        return cryptBytes(DES_ALGORITHM, DES_ALGORITHM, content, key, true);
    }

    /**
     * 通过DES算法对文件加密
     *
     * @param filePath
     *            文件的绝对路径
     * @param key
     *            密钥
     * @return 成功返回true，失败返回false
     * @throws XCryptionException
     * @throws NullPointerException
     */
    public  boolean encryptFileForDES(byte[] key, String sourceFilePath,
            String targetFilePath) throws NullPointerException, XCryptionException {
        return cryptFile(DES_ALGORITHM, DES_ALGORITHM, key, sourceFilePath, targetFilePath, true);
    }

    /**
     * 用DES算法对字符串解密
     *
     * @param content
     *            需要解密的字符串的内容
     * @param key
     *            密钥
     * @return 解密过后得到byte数组
     * @throws XCryptionException
     */
    public  byte[] decryptBytesForDES(byte[] content, byte[] key)
            throws XCryptionException {
        return cryptBytes(DES_ALGORITHM, DES_ALGORITHM, content, key, false);
    }

    /**
     * 通过DES算法对文件解密
     *
     * @param filePath
     *            文件的绝对路径
     * @param key
     *            密钥
     * @return 成功返回true，失败返回false
     * @throws XCryptionException
     * @throws NullPointerException
     */
    public  boolean decryptFileForDES(byte[] key, String sourceFilePath,
            String targetFilePath) throws NullPointerException, XCryptionException {
        return cryptFile(DES_ALGORITHM, DES_ALGORITHM, key, sourceFilePath, targetFilePath, false);
    }

    /**
     * 用3DES算法对字符串加密
     *
     * @param content
     *            需要加密的字符串的内容
     * @param key
     *            密钥
     * @return 加密过后得到byte数组
     * @throws XCryptionException
     */
    public  byte[] encryptBytesFor3DES(byte[] content, byte[] key)
            throws XCryptionException {
        return cryptBytes(TRIPLE_DES_ALGORITHM, KEY_ALOGRITHEM, content, key, true);
    }

    /**
     * 用3DES算法对字符串解密
     *
     * @param content
     *            需要解密的字符串的内容
     * @param key
     *            密钥
     * @return 解密过后得到byte数组
     * @throws XCryptionException
     */
    public  byte[] decryptBytesFor3DES(byte[] content, byte[] key)
            throws XCryptionException {
        return cryptBytes(TRIPLE_DES_ALGORITHM, KEY_ALOGRITHEM, content, key, false);
    }

    /**
     * 对称加解密字节数组并返回经过解密的数据
     *
     * @param content
     *            需要加解密的数据
     * @param sKey
     *            密钥
     * @param isEncrypt
     *            标示加密还是解密，true：加密，false：解密。
     * @return 经过加解密的数据
     * @throws Exception
     */
    private  byte[] cryptBytes(String cryptAlogrithem, String keyAlogrithem,
            byte[] content, byte[] sKey,
            boolean isEncrypt) throws XCryptionException {
        if(null == sKey || XStringUtils.isEmptyString(new String(sKey))) {
            XLog.e(CLASS_NAME, KEY_EMPTY_ERROR);
            throw new NullPointerException(KEY_EMPTY_ERROR);
        }
        if(null == content || 0 == content.length) {
            XLog.e(CLASS_NAME, DATA_EMPTY_ERROR);
            throw new NullPointerException(DATA_EMPTY_ERROR);
        }
        try {
            Cipher cipher = prepareConvertion(cryptAlogrithem, keyAlogrithem, sKey,
                    isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE);
            if (null == cipher) {
                XLog.e(CLASS_NAME, CRYPTION_ERROR);
                throw new XCryptionException(CRYPTION_ERROR);
            }
            return cipher.doFinal(content);
        } catch (Exception e) {
            XLog.e(CLASS_NAME, CRYPTION_ERROR);
            throw new XCryptionException(CRYPTION_ERROR);
        } catch(OutOfMemoryError e) {
            XLog.e(CLASS_NAME, OUT_OF_MEMORY_ERROR);
            throw new XCryptionException(CRYPTION_ERROR);
        }
    }


    /**
     * 对称加解密文件操作并返回操作后的文件绝对路径
     *
     * @param sKey
     *            密钥
     * @param sourceFilePath
     *            需要解密的文件的绝对路径de
     * @param targetFilePath
     *            经过解密得到的文件的绝对路径
     * @param isEncrypt
     *            标示加密还是解密，true：加密，false：解密。
     * @return 解密后文件的绝对路径
     */
    private  boolean cryptFile(String cryptAlogrithem, String keyAlogrithem,
            byte[] sKey, String sourceFilePath,
            String targetFilePath, boolean isEncrypt)
            throws NullPointerException, XCryptionException {
        // 检查要加解密的key为空
        if(null == sKey || XStringUtils.isEmptyString(new String(sKey))) {
            XLog.e(CLASS_NAME, KEY_EMPTY_ERROR);
            throw new NullPointerException(KEY_EMPTY_ERROR);
        }
        try {
            // 从解密前文件读入数据到缓冲区
            byte[] inOutb = XFileUtils.readBytesFromFile(sourceFilePath);
            //TODO:目前为了和服务器兼容，文件加解密有base64转码操作，以后考虑去掉。
            if(!isEncrypt) {
                inOutb = XBase64.decode(inOutb, XBase64.NO_WRAP);
            }
            //解密缓冲区数据
            byte[] result = cryptBytes(cryptAlogrithem, keyAlogrithem, inOutb, sKey, isEncrypt);
            if(isEncrypt) {
                result = XBase64.encode(result, XBase64.NO_WRAP);
            }
            // 把缓冲区数据写入解密后文件
            return XFileUtils.writeFileByByte(targetFilePath, result);
        } catch (Exception e) {
            XLog.e(CLASS_NAME, CRYPTION_ERROR);
            throw new XCryptionException(CRYPTION_ERROR);
        } catch(OutOfMemoryError e) {
            XLog.e(CLASS_NAME, OUT_OF_MEMORY_ERROR);
            throw new XCryptionException(CRYPTION_ERROR);
        }
    }

    /**
     * 为加解密转换做准备
     *
     * @param sKey
     *            密钥
     * @param mode
     *            加解密的模式：ENCRYPT_MODE 和 DECRYPT_MODE
     * @return Cipher
     */
    private  Cipher prepareConvertion(String cryptAlogrithem, String keyAlogrithem,
            byte[] sKey, int mode)
            throws XCryptionException {
        // 检查要加解密的key为空
        if(null == sKey || XStringUtils.isEmptyString(new String(sKey))) {
            XLog.e(CLASS_NAME, KEY_EMPTY_ERROR);
            throw new NullPointerException(KEY_EMPTY_ERROR);
        }
        Cipher cipher = null;
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(keyAlogrithem);
            byte[] keyData = sKey;
            KeySpec keySpec = keyAlogrithem == KEY_ALOGRITHEM ?
                    new DESedeKeySpec(keyData) : new DESKeySpec(keyData);
            Key key = keyFactory.generateSecret(keySpec);
            cipher = Cipher.getInstance(cryptAlogrithem);
            if(keyAlogrithem == KEY_ALOGRITHEM ) {
                IvParameterSpec ivSpec = new IvParameterSpec(new byte[8]);
                cipher.init(mode, key, ivSpec);
            } else {
                cipher.init(mode, key);
            }

        } catch (Exception e) {
            XLog.e(CLASS_NAME, CONVERTION_ERROR);
            throw new XCryptionException(CONVERTION_ERROR);
        }
        return cipher;
    }
}
