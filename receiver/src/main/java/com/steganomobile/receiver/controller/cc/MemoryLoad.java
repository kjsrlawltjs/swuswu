package com.steganomobile.receiver.controller.cc;

import android.content.Context;

import com.steganomobile.common.Methods;
import com.steganomobile.receiver.controller.DataCollector;

public class MemoryLoad extends ResourcesImpl {
    private static final String TAG = MemoryLoad.class.getSimpleName();

    public MemoryLoad(Context context, DataCollector collector) {
        super(context, collector);
    }

    @Override
    public void runCc(String action) {
        final long value = Methods.readMemoryUsage(getPid());
        float data = (value - getPreviousValueMemory()) / 10;
        int element = Math.round(data);
        long delta = value - (element * 10) - getPreviousValueMemory();
        getCollector().setData((byte) (element - 127));
        setPreviousValueMemory(getPreviousValueMemory() + delta);
    }
}