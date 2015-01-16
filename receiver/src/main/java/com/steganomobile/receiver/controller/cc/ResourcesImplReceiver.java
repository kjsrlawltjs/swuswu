package com.steganomobile.receiver.controller.cc;

import android.content.Context;

import com.steganomobile.common.Const;
import com.steganomobile.common.Methods;
import com.steganomobile.receiver.controller.DataCollector;

public abstract class ResourcesImplReceiver extends CcImplReceiver {
    private static final String TAG = ResourcesImplReceiver.class.getSimpleName();
    private long previousValueMemory;
    private long previousValueCpu;
    private int pid;

    public ResourcesImplReceiver(Context context, DataCollector collector) {
        super(collector);
        pid = Methods.getPidOfAplication(TAG, context, Const.PACKAGE_STEGANO_SENDER);
        previousValueCpu = Methods.readPidUsage(TAG, pid);
        previousValueMemory = Methods.readMemoryUsage(pid);
    }

    public long getPreviousValueCpu() {
        return previousValueCpu;
    }

    public void setPreviousValueCpu(long previousValueCpu) {
        this.previousValueCpu = previousValueCpu;
    }

    public int getPid() {
        return pid;
    }

    public long getPreviousValueMemory() {
        return previousValueMemory;
    }

    public void setPreviousValueMemory(long previousValueMemory) {
        this.previousValueMemory = previousValueMemory;
    }
}