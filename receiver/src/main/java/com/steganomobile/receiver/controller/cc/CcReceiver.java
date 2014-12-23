package com.steganomobile.receiver.controller.cc;

public interface CcReceiver {
    public void onStart();

    public void onReceive(String action);

    public void onFinish();
}
