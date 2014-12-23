package com.steganomobile.sender.controller.cc;

import android.content.Context;

import com.steganomobile.common.sender.model.CcSync;

public interface CcSender {
    public void onSend(Context context, int element);

    public void onRestart();

    public void onFinish();

    public void onSync(Context context, CcSync sync, byte element);
}
