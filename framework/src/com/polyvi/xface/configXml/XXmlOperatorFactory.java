
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

package com.polyvi.xface.configXml;

/**
 * 该类负责产生不同的xml操作对象
 */
public class XXmlOperatorFactory {
    /** 对app.xml解密的公共密钥 */
    private static String mPublicKeyForAppConfig;

    /**
     * 设置app.xml解密的公共密钥
     *
     * @param key
     *            公共密钥
     */
    public static void setAppConfigPublicKey(String key) {
        mPublicKeyForAppConfig = key;
    }

    /**
     * 根据是否设置密钥产生不同的类型的appParser
     *
     * @return
     */
    public static XAbstractAppConfigParser createAppConfigParser() {
        return (null == mPublicKeyForAppConfig) ? new XAppConfigParser()
                : new XSecurityAppConfigParser(mPublicKeyForAppConfig);
    }

    public static XCertificateConifgParser createCertificateConifgParser()
    {
        return new XCertificateConifgParser();
    }

}
