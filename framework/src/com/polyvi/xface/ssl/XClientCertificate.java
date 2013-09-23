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

package com.polyvi.xface.ssl;

import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
/**
 *用来表示客户端证书的信息，在initClientCertificate方法中
 */
public class XClientCertificate
{
    protected String mAlgorithm;
    protected String mCertificateName;
    protected String mPassword;
    protected TrustManager[] mTrustManagers;

    public XClientCertificate(String fileName,String password,String algorithm)
    {
        this.mCertificateName = fileName;
        this.mPassword = password;
        this.mAlgorithm = algorithm;
        this.mTrustManagers = createTrustManager();
    }

    private TrustManager[] createTrustManager()
    {
        return new TrustManager[]{ new X509TrustManager()
        {
            public X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }
            public void checkClientTrusted( X509Certificate[] certs, String authType )
            {
            }
            public void checkServerTrusted( X509Certificate[] certs, String authType )
            {
            }
        } };
    }

    public String getAlgorithm()
    {
        return this.mAlgorithm;
    }

    public void setAlgorithm( String algorithm )
    {
        this.mAlgorithm = algorithm;
    }

    public String getCertificateName()
    {
        return this.mCertificateName;
    }

    public void setFileName( String certificateName )
    {
        this.mCertificateName = certificateName;
    }

    public String getPassword()
    {
        return this.mPassword;
    }

    public void setPassword( String password )
    {
        this.mPassword = password;
    }

    public TrustManager[] getTrustManagers()
    {
        return this.mTrustManagers;
    }

    public void setTrustManagers( TrustManager[] trustManagers )
    {
        this.mTrustManagers = trustManagers;
    }
}
