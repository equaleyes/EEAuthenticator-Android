package com.equaleyes.qrcodeandroid.reader;

import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by zan on 8/24/16.
 */
public interface OnCodeDetected {
    void codeDetected(Barcode code);
}
