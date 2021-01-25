package com.bitorb.api.pub.tests;


import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HashUtils.class);
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final ThreadLocal<Mac> MAC = ThreadLocal.withInitial(() -> {
        try {
            return Mac.getInstance(HMAC_SHA256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    });

    private HashUtils() {}

    @Nullable
    public static String getSecretHash(String secret, String content) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            final Mac mac = MAC.get();
            mac.init(keySpec);
            byte[] bytes = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (InvalidKeyException e) {
            LOG.error("Cannot validate secret hash!", e);
            return null;
        }
    }
}
