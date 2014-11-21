package com.steganomobile.receiver.controller;

import android.content.Context;

import com.steganomobile.receiver.controller.cc.CcImpl;

public interface Sync {

    public CcImpl getCc();

    public void finish(Context context);

}
