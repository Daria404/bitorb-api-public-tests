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

    public static void main(String[] args) {
        String s = "!/f31<rq)6OdPF>KuySkqu3bFTHj+@_$eXjc+;UcxT%j8Y&G_%LlZS!>5SEn40kuA6_DUI((!@VeOsyK/h0P!p-wV7WvO?!7Lxq%vgZ5I!>!o*2T1mF!Y+FnVmp%wXjbu#cSr!9;Z8BIGXzPV.(knuh.PI;+GAxTl1!i-zcSy#l/rJ!.<m3s@aopL/.!k!fGQCky#T<h68W/TOk6oh#RV!l0fxfH3!s6wp%>%eB1fNG(Svxd-X0@'t%0oV/!2-z;#zvvPjPo9SAjCQnm.B+cvJyW'wR*k<AgC'h8HVl;+JPd+#ZwVecf(J#1k_XgHa1611749038759/api/v1/order\"clientReqID\":1,\"symbol\":\"BTC_USD_P0\",\"side\":\"SELL\",\"qty\":1.0,\"ordType\":\"2\",\"leverage\":100.0,\"price\":31200.0";
        String m = "12345";

        String secretHash = getSecretHash(m, s);
        System.err.println(secretHash);


        System.err.println("mlkpiv11c/gvcPbL51+tESZdE0sSoyQxkJz/b4Ri2sY=".equals(secretHash));
    }
}
