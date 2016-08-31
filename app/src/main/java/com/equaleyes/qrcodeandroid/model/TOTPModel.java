package com.equaleyes.qrcodeandroid.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.equaleyes.qrcodeandroid.auth.QRAuth;
import com.equaleyes.qrcodeandroid.auth.TOTPGenerator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zan on 8/24/16.
 */
public class TOTPModel extends BaseObservable {
    private final String issuer;
    private final String label;
    private final TOTPGenerator generator;

    public TOTPModel(QRAuth code) throws Exception {
        generator = code.getGenerator();
        issuer = code.getIssuer();
        label = code.getLabel();
    }

    public String getIssuer() {
        return issuer;
    }

    public String getCode() {
        return generator.getCode();
    }

    public long getTimer() {
        return generator.getTimeLeft();
    }

    public String getLabel() {
        return label;
    }
}
