package com.equaleyes.qrcodeandroid.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zan on 8/24/16.
 */
public class QRAuth implements Serializable {
    private final String algorithm;
    private final String secret;
    private final String label;
    private final String issuer;
    private final int digits;
    private final int period;

    public QRAuth(String url) throws Exception {
        URL codeUrl = new URL(null, url, new OTPURLHandler());

        String protocol = codeUrl.getProtocol();
        String host = codeUrl.getHost();
        String path = codeUrl.getPath();
        if (!path.contains(":"))
            path = ":" + path;

        if (!"otpauth".equals(protocol)) {
            throw new Exception("URL not valid otpauth url.");
        }

        if (!"totp".equals(host)) {
            throw new Exception("otpauth not TOTP!");
        }

        String query = codeUrl.getQuery();

        Map<String, String> parameters = parseQuery(query);

        String algorithm = parameters.get("algorithm");
        String secret = parameters.get("secret");
        String digits = parameters.get("digits");
        String period = parameters.get("period");
        String issuer = parameters.get("issuer");

        if (algorithm == null) {
            algorithm = "SHA1";
        }

        if (secret == null) {
            throw new Exception("Secret cannot be null!");
        }

        if (digits == null) {
            digits = "6";
        }

        if (period == null) {
            period = "30";
        }

        this.algorithm = algorithm;
        this.secret = secret;
        this.label = path.split(":")[1];

        if (issuer == null) {
            issuer = path.split(":")[0];
        }

        this.issuer = issuer;

        this.digits = Integer.parseInt(digits);
        if (this.digits < 6 || this.digits > 8) {
            throw new Exception("Invalid digit count!");
        }

        this.period = Integer.parseInt(period);
        if (this.period <= 0 || this.period > 300) {
            throw new Exception("Invalid period.");
        }
    }

    public TOTPGenerator getGenerator() throws Exception {
        return new TOTPGenerator(secret, algorithm, digits, period);
    }

    public void save(Context context) {
        ArrayList<QRAuth> codes = load(context);
        if (codes.indexOf(this) != -1)
            return;

        codes.add(this);
        write(context, codes);
    }

    public void delete(Context context) {
        ArrayList<QRAuth> codes = load(context);
        codes.remove(this);

        write(context, codes);
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<QRAuth> load(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("qr_auth", Context.MODE_PRIVATE);
        String encoded = preferences.getString("qr_auth.list", null);

        if (encoded == null) {
            return new ArrayList<>();
        }

        byte[] serialized = Base64.decode(encoded, Base64.NO_WRAP);
        ByteArrayInputStream input = new ByteArrayInputStream(serialized);
        try {
            ObjectInputStream objInput = new ObjectInputStream(input);
            return (ArrayList<QRAuth>) objInput.readObject();
        } catch (Exception e) {
            Log.e("QRAuth", "Unable to serialize Auth:", e);
        }

        return new ArrayList<>();
    }

    private void write(Context context, ArrayList<QRAuth> codes) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objOutput = new ObjectOutputStream(output);
            objOutput.writeObject(codes);

            byte[] serialized = output.toByteArray();

            SharedPreferences preferences = context.getSharedPreferences("qr_auth", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("qr_auth.list", Base64.encodeToString(serialized, Base64.NO_WRAP));
            editor.apply();
        } catch (IOException e) {
            Log.e("QRAuth", "Unable to serialize Auth:", e);
        }
    }

    private Map<String, String> parseQuery(String query) {
        String[] keyValuePairs = query.split("&");

        Map<String, String> parameters = new HashMap<>();

        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length != 2)
                continue;

            parameters.put(keyValue[0], keyValue[1]);
        }

        return parameters;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof QRAuth) {
            QRAuth otherQR = (QRAuth) other;
            return secret.equals(otherQR.secret);
        }
        return false;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getLabel() {
        return label;
    }

    private class OTPURLHandler extends URLStreamHandler {
        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            return null;
        }
    }
}
