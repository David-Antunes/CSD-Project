package com.csd.blockneat;

import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.client.BlockNeatAPIClient;
import com.csd.blockneat.client.ECDSASignature;
import com.csd.blockneat.client.InternalUser;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class Client {
    public static void main(String[] args) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, NoSuchProviderException, InterruptedException, SignatureException, InvalidKeyException {
        String url = "https://172.20.0.2:8443";
        System.getProperties().setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
//        String url = "https://localhost:8080";
        ECDSASignature ec = new ECDSASignature(ECDSASignature.fetchKeyPair("config/users.pkcs12", "users", "user1", "user1"));
        InternalUser user = new InternalUser("user1", ec);
        BlockNeatAPI bna = new BlockNeatAPIClient(user, url);
        System.out.println(bna.createUser());
        System.out.println(bna.getAllUsers());
    }

}
