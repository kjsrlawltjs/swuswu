package com.steganomobile.sender.controller.cc;

import android.content.Context;

import com.steganomobile.common.Const;

public abstract class SystemImplSender extends CcImplSender {
    private static final String TAG = SystemImplSender.class.getSimpleName();
    private int interval;

    public SystemImplSender(int interval) {
        this.interval = interval;
    }

    @Override
    public void onSend(Context context, int element) {
        super.onSend(context, element);
    }

    protected void sendSystem(int element) {

        long endTime = System.currentTimeMillis() + interval - 1;
        if (element == Const.UP_NUMBER) {
            while (System.currentTimeMillis() < endTime) {
                System.getenv();
            }
        }
    }
}
