package com.csd.blockneat.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;


public class ECDSASignature {

    private final KeyPair keyPair;
    private final Signature signature;

    public ECDSASignature(KeyPair keyPair) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        this.keyPair = keyPair;
        this.signature = Signature.getInstance("SHA512withECDSA");
    }

    public byte[] sign(byte[] payload) throws InvalidKeyException, SignatureException {
        signature.initSign(keyPair.getPrivate());
        signature.update(payload);
        return signature.sign();
    }

    public boolean verifySignature(byte[] payload, byte[] sig) throws InvalidKeyException, SignatureException {
        signature.initVerify(keyPair.getPublic());
        signature.update(payload);
        return signature.verify(sig);
    }

    public byte[] getPublicKey() {
        return this.keyPair.getPublic().getEncoded();
    }

    public static KeyPair fetchKeyPair(String filename, String password, String user, String userPassword) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {

        // SEPARATE IN DIFFERENT FILES
        FileInputStream is = new FileInputStream(filename);

        KeyStore keystore = KeyStore.getInstance("pkcs12");
        keystore.load(is, password.toCharArray());

        KeyPair kp;
        Key key = keystore.getKey(user, userPassword.toCharArray());
        if (key instanceof PrivateKey) {

            Certificate cert = keystore.getCertificate(user);
            // Get now public key
            PublicKey publicKey = cert.getPublicKey();
            // Get the KeyPair
            kp = new KeyPair(publicKey, (PrivateKey) key);
            // Get again the Public and Private Key from the KeyPair
            is.close();

        } else {
            throw new CertificateException();
        }
        return kp;
    }
}
