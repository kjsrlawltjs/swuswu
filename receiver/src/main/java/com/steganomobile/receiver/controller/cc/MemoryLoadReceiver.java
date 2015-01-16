package com.steganomobile.receiver.controller.cc;

import android.content.Context;

import com.steganomobile.common.Methods;
import com.steganomobile.receiver.controller.DataCollector;

public class MemoryLoadReceiver extends ResourcesImplReceiver {
    private static final String TAG = MemoryLoadReceiver.class.getSimpleName();

    public MemoryLoadReceiver(Context context, DataCollector collector) {
        super(context, collector);
    }

    @Override
    public void onReceive(String action) {
        final long value = Methods.readMemoryUsage(getPid());
        float data = (value - getPreviousValueMemory()) / 10;
        int element = Math.round(data);
        long delta = value - (element * 10) - getPreviousValueMemory();
        getCollector().setData((byte) (element - 127));
        setPreviousValueMemory(getPreviousValueMemory() + delta);
    }
}