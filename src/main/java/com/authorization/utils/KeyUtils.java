package com.authorization.utils;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class KeyUtils {
    private KeyUtils() {
    }

    public static RSAPublicKey parsePublicKey(String pem) throws Exception {
        String stripped = stripHeaderFooter(pem, "PUBLIC KEY");
        byte[] decoded = Base64.getDecoder().decode(stripped);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static RSAPrivateKey parsePrivateKey(String pem) throws Exception {
        String stripped = stripHeaderFooter(pem, "PRIVATE KEY");
        byte[] decoded = Base64.getDecoder().decode(stripped);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private static String stripHeaderFooter(String pem, String keyType) {
        String header = "-----BEGIN " + keyType + "-----";
        String footer = "-----END " + keyType + "-----";

        // Remove header/footer
        String content = pem.replace(header, "").replace(footer, "");

        // Remove all whitespace (newline, tabs)
        return content.replaceAll("\\s", "");
    }
}
