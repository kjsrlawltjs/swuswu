package com.steganomobile.sender.controller.cc;

import android.content.Context;

public class SystemLoadSender extends SystemImplSender {
    private static final String TAG = SystemLoadSender.class.getSimpleName();

    public SystemLoadSender(int interval) {
        super(interval);
    }

    @Override
    public void onSend(Context context, int element) {
        super.onSend(context, element);

        sendSystem(element);
    }
}
