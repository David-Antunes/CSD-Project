package com.csd.blockneat.client;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.HashSet;
import java.util.Set;
import java.util.Base64;
public class InternalUser {
    ECDSASignature signature;

    String username;
    public InternalUser(String username, ECDSASignature signature) {
        this.signature = signature;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(signature.getPublicKey());
    }

    public String sign(String message) throws SignatureException, InvalidKeyException {
            return Base64.getEncoder().encodeToString(signature.sign(message.getBytes()));
    }
}
