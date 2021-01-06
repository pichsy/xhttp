package com.pichs.xhttp.util;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.X509TrustManager;

/**
 * Created by hui.yang on 16/12/27.
 */

public class HttpTrustManager implements X509TrustManager {
    private String oMd5;
    private X509TrustManager systemTrustManager;

    public HttpTrustManager(X509TrustManager x509TrustManager, String md5) {
        if (x509TrustManager == null) {
            throw new AssertionError();
        }
        systemTrustManager = x509TrustManager;
        this.oMd5 = md5;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (systemTrustManager != null) {
            systemTrustManager.checkClientTrusted(chain, authType);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (systemTrustManager != null) {
            systemTrustManager.checkServerTrusted(chain, authType);
        }
        for (X509Certificate cert : chain) {
            String subjectStr = cert.getSubjectDN().getName();
            ArrayList rdns = null;
            try {
                rdns = (ArrayList) (new Rfc2253Parser(subjectStr)).parseDn();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (rdns != null) {
                for (Object rdn : rdns) {
                    String type = ((Rdn) rdn).getType();
                    if ("O".equals(type)) {
                        String org = (String) ((Rdn) rdn).getValue();
                        if (oMd5.equalsIgnoreCase(MD5.encode2Hex(org))) {
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        if (systemTrustManager != null) {
            return systemTrustManager.getAcceptedIssuers();
        } else {
            return new X509Certificate[0];
        }
    }
}
