package com.equaleyes.qrcodeandroid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.equaleyes.qrcodeandroid.auth.QRAuth;
import com.equaleyes.qrcodeandroid.databinding.ActivityCodeListBinding;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.SdkConfig;

import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class QRAuthTest {
    private final String AUTH_URL = "otpauth://totp/Equaleyes:RGTkY?algorithm=SHA1&digits=6&issuer=Equaleyes&period=30&secret=FHWXZPLHOD3MZBU6";

    private Activity activity;
    private QRAuth code;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(Activity.class);
        code = new QRAuth(AUTH_URL);
    }

    @After
    public void tearDown() {
        SharedPreferences preferences = activity.getSharedPreferences("qr_auth", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    @Test
    public void testCanParseUrl() throws Exception {
        assertNotNull(code);
    }

    @Test
    public void testSaveQRAuthCode() throws Exception {
        code.save(activity);

        List<QRAuth> codes = QRAuth.load(activity);
        assertTrue(codes.contains(code));
    }

    @Test
    public void testDeleteQRAuthCode() throws Exception {
        code.save(activity);

        List<QRAuth> codes = QRAuth.load(activity);
        assertTrue(codes.contains(code));

        code.delete(activity);
        codes = QRAuth.load(activity);
        assertFalse(codes.contains(code));
    }
}