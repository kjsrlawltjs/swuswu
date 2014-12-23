package com.steganomobile.receiver.controller;

import android.content.Context;

import com.steganomobile.receiver.controller.cc.CcImplReceiver;

public interface Sync {

    public CcImplReceiver getCc();

    public void onFinish(Context context);

}
