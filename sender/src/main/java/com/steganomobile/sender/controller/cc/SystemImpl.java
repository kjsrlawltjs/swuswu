package com.steganomobile.sender.controller.cc;

import android.content.Context;

import com.steganomobile.common.Const;

public abstract class SystemImpl extends CcImpl {
    private static final String TAG = SystemImpl.class.getSimpleName();
    private int interval;

    public SystemImpl(int interval) {
        this.interval = interval;
    }

    @Override
    public void sendCc(Context context, int element) {
        super.sendCc(context, element);
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
