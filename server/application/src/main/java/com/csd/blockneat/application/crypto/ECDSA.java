package com.csd.blockneat.application.crypto;

import com.csd.blockneat.application.entities.Signed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

@Component
public class ECDSA {

    private final boolean blockmess;
    private PrivateKey privateKey;
    private final int replicaId;
    public ECDSA(@Value("${blockmess.enabled}") String blockmess) {
        this.blockmess = Boolean.parseBoolean(blockmess);
        String replica_id = System.getenv("REPLICA_ID");
        replicaId = replica_id != null ? Integer.parseInt(replica_id) : 0;

        try {
            String key = Files.readString(Path.of("./config/replicaKeys/ec-secp256k1-priv-key-pkcs8-" + replicaId + ".pem"));
            String privateKeyPEM = key
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("\n", "")
                    .replace("-----END PRIVATE KEY-----", "");
            byte[] decodedKey = Base64.getDecoder().decode(privateKeyPEM);

            privateKey = KeyFactory
                    .getInstance("EC", "BC")
                    .generatePrivate(new PKCS8EncodedKeySpec(decodedKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean verifySign(String certBase64, String signBase64, String message) {
        try {
            PublicKey ecdsa = KeyFactory
                    .getInstance("EC")
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(certBase64.getBytes(StandardCharsets.UTF_8))));

            Signature signature = Signature.getInstance("SHA512withECDSA");
            signature.initVerify(ecdsa);
            signature.update(message.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(signBase64.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String sign(String message) {
        try {
            Signature signature = Signature.getInstance("SHA512withECDSA", "BC");
            signature.initSign(privateKey, new SecureRandom());
            signature.update(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public <T> Signed<T> of(T object) {
        String signature = sign(object.toString());
        Map<Integer, String> signatures = new TreeMap<>();
        signatures.put(replicaId, signature);
        return new Signed<>(object, blockmess ? null : signatures);
    }
}
