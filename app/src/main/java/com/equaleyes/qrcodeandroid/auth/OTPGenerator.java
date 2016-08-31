package com.equaleyes.qrcodeandroid.auth;

import android.annotation.SuppressLint;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zan on 8/24/16.
 */
public abstract class OTPGenerator {
    private final int[] pinModTable = new int[]{
            0,
            10,
            100,
            1000,
            10000,
            100000,
            1000000,
            10000000,
            100000000,
    };

    private final String secret;
    private final String algorithm;
    private final int digits;

    public OTPGenerator(String secret, String algorithm, int digits) throws Exception {
        this.secret = secret;
        this.algorithm = algorithm;
        this.digits = digits;

        if (!Arrays.asList("SHA1", "SHA256", "SHA512", "SHAMD5").contains(algorithm)) {
            throw new Exception("Unsupported algorithm");
        }
    }

    abstract public String getCode();

    @SuppressLint("DefaultLocale")
    protected String generateOTPForCounter(long counter) throws Base32String.DecodingException {
        byte[] secretBytes = Base32String.decode(secret);
        byte[] digested;
        try {
            digested = hmacDigest(longToBytes(counter), secretBytes);
        } catch (Exception e) {
            Log.e("OTPGenerator", "Unable to digest:", e);
            return "";
        }

        int offset = digested[digested.length - 1] & 0x0F;
        byte[] truncatedBytes = new byte[4];
        System.arraycopy(digested, offset, truncatedBytes, 0, 4);

        int truncatedInt = bytesToInt(truncatedBytes) & 0x7FFFFFFF;

        int pinValue = truncatedInt % pinModTable[digits];

        return Integer.toString(pinValue);
    }

    private byte[] hmacDigest(byte[] value, byte[] key) throws Exception {
        String algorithmName = "";
        switch (algorithm) {
            case "SHA1":
            case "SHA256":
            case "SHA512":
                algorithmName = "Hmac" + algorithm;
                break;
            case "SHAMD5":
                algorithmName = "HmacMD5";
                break;
        }

        SecretKeySpec signingKey = new SecretKeySpec(key, algorithmName);

        Mac mac = Mac.getInstance(algorithmName);
        mac.init(signingKey);

        return mac.doFinal(value);
    }

    private byte[] longToBytes(long x) {
        return ByteBuffer.allocate(8).putLong(x).array();
    }

    private int bytesToInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN);

        return buffer.getInt();
    }
}
