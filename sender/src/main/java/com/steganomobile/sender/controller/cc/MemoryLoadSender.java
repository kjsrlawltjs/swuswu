package com.steganomobile.sender.controller.cc;

import android.content.Context;

public class MemoryLoadSender extends CcImplSender {
    private static final String TAG = MemoryLoadSender.class.getSimpleName();

    static {
        System.loadLibrary("stegano");
    }

    public native void allocateStegano(int stegano);

    public native void freeStegano();

    @Override
    public void onSend(Context context, int element) {
        super.onSend(context, element);
        allocateStegano(element + 127);
    }

    @Override
    public void onRestart() {
        freeStegano();
    }
}
