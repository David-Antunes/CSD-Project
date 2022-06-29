package com.csd.blockneat.crypto;

import com.csd.blockneat.requests.Response;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class BlockneatSignatures {

    private final ECDSA sign;
    private final Map<Integer, PublicKey> replicasPubKeys;
    private final Signature signature;

    public BlockneatSignatures(int replicas) {
        this.sign = new ECDSA();
        this.replicasPubKeys = new TreeMap<>();
        try {
            signature = Signature.getInstance("SHA512withECDSA", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        for (int replicaId = 0; replicaId < replicas; replicaId++) {
            try {
                String key = Files.readString(Path.of("./config/replicaKeys/ec-secp256k1-pub-key" + replicaId + ".pem"));
                String publicKeyPEM = key
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replaceAll("\n", "")
                        .replace("-----END PUBLIC KEY-----", "");
                byte[] decodedKey = Base64.getDecoder().decode(publicKeyPEM);

                PublicKey publicKey = KeyFactory
                        .getInstance("EC", "BC")
                        .generatePublic(new X509EncodedKeySpec(decodedKey));
                replicasPubKeys.put(replicaId, publicKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ECDSA getSign() {
        return sign;
    }

    public boolean verifySignatures(Response response) {
        Map<Integer, String> signatures = response.response().signBase64();
        try {

            for (var entry : signatures.entrySet()) {
                byte[] replicaSignature = Base64.getDecoder().decode(entry.getValue());
                signature.initVerify(replicasPubKeys.get(entry.getKey()));
                signature.update(response.response().object().toString().getBytes(StandardCharsets.UTF_8));
                if(!signature.verify(replicaSignature))
                    return false;
            }
            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
