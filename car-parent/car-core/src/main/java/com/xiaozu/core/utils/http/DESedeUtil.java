package com.xiaozu.core.utils.http;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class DESedeUtil {

    public static final int ENCRYPT_MODE = Cipher.ENCRYPT_MODE;
    public static final int DECRYPT_MODE = Cipher.DECRYPT_MODE;

    private static final String ALGORITHM = "DESede";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private Cipher cipher;
    private int opmode;

    public DESedeUtil(int mode, String key) {
        try {
            cipher = Cipher.getInstance(ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Key secKey = getSecKey(key);
        try {
            cipher.init(mode, secKey, new SecureRandom());
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        opmode = mode;
    }

    private static Key getSecKey(String key) {
        SecretKey securekey;
        try {
            byte[] material = Arrays.copyOf(Base64.getDecoder().decode(key.getBytes(UTF8)), 24);
            DESedeKeySpec keySpec = new DESedeKeySpec(material);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            securekey = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return securekey;
    }

    public String encrypt(String data) {
        if (opmode != ENCRYPT_MODE) {
            return null;
        }
        if (data == null) {
            return null;
        }
        byte[] encData;
        try {
            encData = cipher.doFinal(data.getBytes(UTF8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (encData == null) {
            return null;
        }
        return new String(Base64.getEncoder().encode(encData), UTF8);
    }

    public String decrypt(String data) {
        return decrypt(data.getBytes(UTF8));
    }

    public String decrypt(byte[] data) {
        if (opmode != DECRYPT_MODE) {
            return null;
        }
        if (data == null || data.length == 0) {
            return null;
        }
        byte[] decData;
        try {
            decData = cipher.doFinal(Base64.getDecoder().decode(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (decData == null || decData.length == 0) {
            return null;
        }
        return new String(decData, UTF8);
    }

}
