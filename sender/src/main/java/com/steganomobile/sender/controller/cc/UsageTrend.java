package com.steganomobile.sender.controller.cc;

import android.content.Context;

public class UsageTrend extends SystemImpl {
    private static final String TAG = UsageTrend.class.getSimpleName();

    public UsageTrend(int interval) {
        super(interval);
    }

    @Override
    public void sendCc(Context context, int element) {
        super.sendCc(context, element);

        sendSystem(element);
    }
}
