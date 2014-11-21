package com.steganomobile.sender.controller.cc;

import android.content.Context;

public class SystemLoad extends SystemImpl {
    private static final String TAG = SystemLoad.class.getSimpleName();

    public SystemLoad(int interval) {
        super(interval);
    }

    @Override
    public void sendCc(Context context, int element) {
        super.sendCc(context, element);

        sendSystem(element);
    }
}
