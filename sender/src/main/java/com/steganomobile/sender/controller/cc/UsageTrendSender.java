package com.steganomobile.sender.controller.cc;

import android.content.Context;

public class UsageTrendSender extends SystemImplSender {
    private static final String TAG = UsageTrendSender.class.getSimpleName();

    public UsageTrendSender(int interval) {
        super(interval);
    }

    @Override
    public void onSend(Context context, int element) {
        super.onSend(context, element);

        sendSystem(element);
    }
}