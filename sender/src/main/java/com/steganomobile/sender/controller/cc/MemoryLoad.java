package com.steganomobile.sender.controller.cc;

import android.content.Context;

public class MemoryLoad extends CcImpl {
    private static final String TAG = MemoryLoad.class.getSimpleName();

    static {
        System.loadLibrary("stegano");
    }

    public native void allocateStegano(int stegano);

    public native void freeStegano();

    @Override
    public void sendCc(Context context, int element) {
        super.sendCc(context, element);
        allocateStegano(element + 127);
    }

    @Override
    public void finishCc() {
        freeStegano();
    }
}
