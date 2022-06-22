package com.csd.blockneat.application.crypto;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Base64;

public class ECDSA {
    public static boolean verifySign(String certBase64, String signBase64, String message) {
        try {
            InputStream targetStream = new ByteArrayInputStream(Base64.getDecoder().decode(certBase64.getBytes(StandardCharsets.UTF_8)));
            Certificate ecdsa = CertificateFactory
                    .getInstance("X509")
                    .generateCertificate(targetStream);
            targetStream.close();

            Signature signature = Signature.getInstance("SHA512withECDSA");
            signature.initVerify(ecdsa);
            signature.update(message.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(signBase64.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return false;
        }
    }

}
