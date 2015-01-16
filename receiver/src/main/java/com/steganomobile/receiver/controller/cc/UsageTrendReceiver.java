package com.steganomobile.receiver.controller.cc;

import android.content.Context;

import com.steganomobile.common.Const;
import com.steganomobile.common.Methods;
import com.steganomobile.receiver.controller.DataCollector;

public class UsageTrendReceiver extends ResourcesImplReceiver {

    private static final String TAG = UsageTrendReceiver.class.getSimpleName();

    public UsageTrendReceiver(Context context, DataCollector collector) {
        super(context, collector);
    }

    @Override
    public void onReceive(String action) {
        long value = Methods.readPidUsage(TAG, getPid());
        long diff = value - getPreviousValueCpu();
        if (diff > 0) {
            if (diff > getCollector().getInfo().getInterval() / 50) {
                getCollector().setData(Const.UP_NUMBER);
            } else {
                getCollector().setData(Const.DOWN_NUMBER);
            }
        } else {
            if (diff < getCollector().getInfo().getInterval() / 50) {
                getCollector().setData(Const.DOWN_NUMBER);
            } else {
                getCollector().setData(Const.UP_NUMBER);
            }
        }
        setPreviousValueCpu(value);
    }
}