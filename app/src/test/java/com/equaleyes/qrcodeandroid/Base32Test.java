package com.equaleyes.qrcodeandroid;

import com.equaleyes.qrcodeandroid.auth.Base32String;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * Created by zan on 8/25/16.
 */
public class Base32Test {
    private final String encoded = "KBQWIZDJNZTQ";
    private final String decoded = "Padding";

    @Test
    public void testBase32StringDecode() throws Exception {
        assertArrayEquals(Base32String.decode(encoded), decoded.getBytes());
    }

    @Test
    public void testBase32StringEncode() throws Exception {
        assertEquals(Base32String.encode(decoded.getBytes()), encoded);
    }
}
