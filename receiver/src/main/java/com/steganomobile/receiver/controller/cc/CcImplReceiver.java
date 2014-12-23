package com.steganomobile.receiver.controller.cc;

import android.util.Log;

import com.steganomobile.receiver.controller.DataCollector;

public abstract class CcImplReceiver implements CcReceiver {
    private static final String TAG = CcImplReceiver.class.getSimpleName();
    private DataCollector collector;

    protected CcImplReceiver(DataCollector collector) {
        onStart();
        this.collector = collector;
    }

    public DataCollector getCollector() {
        return collector;
    }

    @Override
    public void onFinish() {
        Log.e(TAG, "Finishing Covert Channel - Receiver");
    }

    @Override
    public void onStart() {
        Log.e(TAG, "Starting Covert Channel - Receiver");
    }
}