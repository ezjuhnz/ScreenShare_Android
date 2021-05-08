package com.bairuitech.anychat;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.util.Base64;

import com.cmbc.av.utils.LogUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

public class AnyChatCertHelper {
    private static final int AC_RSA_PKCS1_OAEP_PADDING_SHE256 = 100;
    private static final String RSA = "RSA";
    private static final String RSA_OAEP_SHA256_PADDING = "RSA/ECB/OAEPWithSHA256AndMGF1Padding";
    private static String beginCertificate = "-----BEGIN CERTIFICATE-----";
    private static String endCertificate = "-----END CERTIFICATE-----";

    public static int GetRSAPaddingMode(int flags) {
        return 100;
    }

    public static String GetX509CertInfo(byte[] certBytes) {
        JSONObject certificateInfo = null;
        try {
            X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certBytes));
            PublicKey pk = cert.getPublicKey();
            if (null == null) {
                certificateInfo = new JSONObject();
            }
            certificateInfo.put("Before", cert.getNotBefore().getTime() / 1000);
            certificateInfo.put("After", cert.getNotAfter().getTime() / 1000);
            certificateInfo.put("OwnerUrl", cropString(cert.getSubjectDN().getName()));
            certificateInfo.put("PubKey", Base64.encodeToString(pk.getEncoded(), 2));
            return certificateInfo.toString();
        } catch (Exception e) {
            LogUtils.eTag("AnyChatCertHelper", "GetX509CertInfo failure", e.fillInStackTrace());
            return null;
        }
    }

    private static String cropString(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        Matcher m = Pattern.compile("(?<=CN\\=).*?(?=,|(s*$))").matcher(s);
        while (m.find()) {
            stringBuilder.append(m.group());
        }
        return stringBuilder.toString();
    }

    public static int VerifyX509Cert(byte[] certChain, byte[] cert2Verify) {
        List<X509Certificate> certs = new ArrayList();
        String chainString = null;
        String str = new String(certChain);
        if (str != null && str.length() > 0) {
            String[] strings = str.split(beginCertificate);
            chainString = beginCertificate + strings[strings.length - 1];
        }
        try {
            CertificateFactory certificatefactory;
            String[] splitCert = splitCert(chainString.getBytes());
            if (VERSION.SDK_INT >= 28) {
                certificatefactory = CertificateFactory.getInstance("X.509");
            } else {
                certificatefactory = CertificateFactory.getInstance("X.509", "BC");
            }
            certs.add((X509Certificate) certificatefactory.generateCertificate(new ByteArrayInputStream(cert2Verify)));
            for (String b : splitCert) {
                if (b.indexOf(beginCertificate) == -1) {
                    break;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(b);
                if (!b.endsWith(endCertificate)) {
                    stringBuilder.append(endCertificate);
                }
                try {
                    certs.add((X509Certificate) certificatefactory.generateCertificate(new ByteArrayInputStream(stringBuilder.toString().getBytes())));
                } catch (Exception e) {
                    LogUtils.eTag("AnyChatCertHelper", "VerifyX509Cert parse failure", e.fillInStackTrace());
                }
            }
            List<X509Certificate> certOrder = order(certs);
            if (certOrder.size() <= 0) {
                return 0;
            }
            X509Certificate lastCert = (X509Certificate) certOrder.get(certOrder.size() - 1);
            if (!lastCert.getIssuerDN().equals(lastCert.getSubjectDN())) {
                X509Certificate rootCert = getRootCert(lastCert);
                if (rootCert == null) {
                    return -1;
                }
                certOrder.add(rootCert);
            }
            verifyCerts(certOrder);
            return 0;
        } catch (Exception e2) {
            LogUtils.eTag("AnyChatCertHelper", "VerifyX509Cert failure", e2.fillInStackTrace());
            return -1;
        }
    }

    private static String[] splitCert(byte[] certChain) {
        return new String(certChain).split(endCertificate);
    }

    private static X509Certificate findParent(List<X509Certificate> parents, X509Certificate child) {
        Principal issuerDN = child.getIssuerDN();
        if (issuerDN.equals(child.getSubjectDN())) {
            return null;
        }
        int i = 0;
        while (i < parents.size()) {
            X509Certificate parent = (X509Certificate) parents.get(i);
            if (parent == null) {
                break;
            } else if (issuerDN.equals(parent.getSubjectDN())) {
                return parent;
            } else {
                i++;
            }
        }
        return null;
    }

    private static List<X509Certificate> order(List<X509Certificate> certss) {
        List<X509Certificate> certInOder = new ArrayList();
        X509Certificate cert2Verify = (X509Certificate) certss.get(0);
        certInOder.add(cert2Verify);
        for (int i = 0; i < certss.size(); i++) {
            X509Certificate parent = findParent(certss, cert2Verify);
            if (parent == null) {
                break;
            }
            certInOder.add(parent);
            cert2Verify = parent;
        }
        return certInOder;
    }

    private static void verifyCerts(List<X509Certificate> certs) throws Exception {
        int n = certs.size();
        for (int i = 0; i < n - 1; i++) {
            ((X509Certificate) certs.get(i)).verify(((X509Certificate) certs.get(i + 1)).getPublicKey());
        }
        X509Certificate last = (X509Certificate) certs.get(n - 1);
        last.verify(last.getPublicKey());
    }

    @SuppressLint({"TrulyRandom"})
    public static byte[] RSA_PublicEncrypt(byte[] data, byte[] publicKey) {
        try {
            RSAPublicKey keyPublic = (RSAPublicKey) KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey, 2)));
            int keySize = keyPublic.getModulus().bitLength();
            Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
            cipher.init(1, keyPublic);
            return rsaSplitCodec(1, data, keySize, cipher);
        } catch (Exception e) {
            LogUtils.eTag("AnyChatCertHelper", "RSA_PublicEncrypt failure", e.fillInStackTrace());
            return null;
        }
    }

    @SuppressLint({"TrulyRandom"})
    public static byte[] RSA_PrivateEncrypt(byte[] data, byte[] privateKey) {
        try {
            RSAPrivateKey keyPrivate = (RSAPrivateKey) KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(getKey(privateKey), 2)));
            int keySize = keyPrivate.getModulus().bitLength();
            Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
            cipher.init(1, keyPrivate);
            return rsaSplitCodec(1, data, keySize, cipher);
        } catch (Exception e) {
            LogUtils.eTag("AnyChatCertHelper", "RSA_PrivateEncrypt failure", e.fillInStackTrace());
            return null;
        }
    }

    @SuppressLint({"TrulyRandom"})
    public static byte[] RSA_PublicDecrypt(byte[] data, byte[] publicKey) {
        try {
            RSAPublicKey keyPublic = (RSAPublicKey) KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey, 2)));
            int keySize = keyPublic.getModulus().bitLength();
            Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
            cipher.init(2, keyPublic);
            return rsaSplitCodec(2, data, keySize, cipher);
        } catch (Exception e) {
            LogUtils.eTag("AnyChatCertHelper", "RSA_PublicDecrypt failure", e.fillInStackTrace());
            return null;
        }
    }

    @SuppressLint({"TrulyRandom"})
    public static byte[] RSA_PrivateDecrypt(byte[] data, byte[] privateKey) {
        try {
            RSAPrivateKey keyPrivate = (RSAPrivateKey) KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(getKey(privateKey), 2)));
            int keySize = keyPrivate.getModulus().bitLength();
            Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
            cipher.init(2, keyPrivate);
            return rsaSplitCodec(2, data, keySize, cipher);
        } catch (Exception e) {
            LogUtils.eTag("AnyChatCertHelper", "RSA_PrivateDecrypt failure", e.fillInStackTrace());
            return null;
        }
    }

    private static byte[] rsaSplitCodec(int mode, byte[] data, int keySize, Cipher cipher) throws Exception {
        int maxBlock;
        int dataLen = data.length;
        if (mode == 2) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = (keySize / 8) - 66;
        }
        if (dataLen <= maxBlock) {
            return cipher.doFinal(data);
        }
        List<Byte> allBytes = new ArrayList();
        int bufIndex = 0;
        byte[] buf = new byte[maxBlock];
        int i = 0;
        while (i < dataLen) {
            buf[bufIndex] = data[i];
            bufIndex++;
            if (bufIndex == maxBlock || i == dataLen - 1) {
                for (byte b : cipher.doFinal(buf)) {
                    allBytes.add(Byte.valueOf(b));
                }
                bufIndex = 0;
                if (i == dataLen - 1) {
                    buf = null;
                } else {
                    buf = new byte[Math.min(maxBlock, (dataLen - i) - 1)];
                }
            }
            i++;
        }
        byte[] bytes = new byte[allBytes.size()];
        i = 0;
        for (Byte b2 : allBytes) {
            int i2 = i + 1;
            bytes[i] = b2.byteValue();
            i = i2;
        }
        return bytes;
    }

    private static byte[] getKey(byte[] s) {
        String keyContent = new String(s);
        return keyContent.substring(keyContent.indexOf("Y-----") + 7, keyContent.lastIndexOf("-----E")).getBytes();
    }

    public static X509Certificate getRootCert(X509Certificate selfCert) {
        try {
            File mDir = new File(new StringBuilder(String.valueOf(System.getenv("ANDROID_ROOT"))).append("/etc/security/cacerts").toString());
            if (mDir == null || !mDir.isDirectory()) {
                return null;
            }
            for (String caFile : mDir.list()) {
                X509Certificate cert = readCertificate(mDir, caFile);
                if (cert == null) {
                    return null;
                }
                if (cropString(cert.getSubjectDN().getName()).equals(cropString(selfCert.getIssuerDN().getName()))) {
                    return cert;
                }
            }
            return null;
        } catch (Exception e) {
            LogUtils.eTag("AnyChatCertHelper", "Get RootCert failure", e.fillInStackTrace());
            return null;
        }
    }

    private static X509Certificate readCertificate(File mDir, String file) {
        InputStream inputStream;
        Exception e;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(new File(mDir, file)));
            try {
                X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
                is.close();
                inputStream = is;
                return x509Certificate;
            } catch (Exception e2) {
                e = e2;
                inputStream = is;
                LogUtils.eTag("AnyChatCertHelper", "Read Certificate failure", e.fillInStackTrace());
                return null;
            }
        } catch (Exception e3) {
            e = e3;
            LogUtils.eTag("AnyChatCertHelper", "Read Certificate failure", e.fillInStackTrace());
            return null;
        }
    }
}
