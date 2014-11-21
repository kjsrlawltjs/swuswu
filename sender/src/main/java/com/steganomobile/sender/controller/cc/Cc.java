package com.steganomobile.sender.controller.cc;

import android.content.Context;

import com.steganomobile.common.sender.model.CcSync;

public interface Cc {
    public void sendCc(Context context, int element);

    public void finishCc();

    public void clearCc();

    public void syncCc(Context context, CcSync sync, byte element);
}
