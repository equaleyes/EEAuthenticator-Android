package com.equaleyes.qrcodeandroid.auth;

import android.util.Log;

/**
 * Created by zan on 8/24/16.
 */
public class TOTPGenerator extends OTPGenerator {
    private final int period;

    public TOTPGenerator(String secret, String algorithm, int digits, int period) throws Exception {
        super(secret, algorithm, digits);
        this.period = period;

        if (period <= 0 || period > 300) {
            throw new Exception("Period is not valid!");
        }
    }

    @Override
    public String getCode() {
        try {
            return generateOTPForCounter((System.currentTimeMillis() / 1000) / period);
        } catch (Base32String.DecodingException e) {
            Log.e("TOTPGenerator", "Base 32 decoding failed!", e);
            return "000000";
        }
    }

    public long getTimeLeft() {
        return period - (System.currentTimeMillis() / 1000) % period;
    }
}
