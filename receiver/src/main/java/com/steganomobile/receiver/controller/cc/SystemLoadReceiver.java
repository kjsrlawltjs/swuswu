package com.steganomobile.receiver.controller.cc;

import android.content.Context;

import com.steganomobile.common.Const;
import com.steganomobile.common.Methods;
import com.steganomobile.receiver.controller.DataCollector;

public class SystemLoadReceiver extends ResourcesImplReceiver {
    private static final String TAG = SystemLoadReceiver.class.getSimpleName();

    public SystemLoadReceiver(Context context, DataCollector collector) {
        super(context, collector);
    }

    @Override
    public void onReceive(String action) {
        long usage = Methods.readPidUsage(TAG, getPid());
//        Log.e(TAG, " " + (usage - getPreviousValueCpu()));
        long diff = usage - getPreviousValueCpu();
        if (diff > getCollector().getInfo().getInterval() / Const.DEFAULT_USAGE_DIVIDER) {
            getCollector().setData(Const.UP_NUMBER);
        } else {
            getCollector().setData(Const.DOWN_NUMBER);
        }
        setPreviousValueCpu(usage);
        // Log.i(TAG, "Usage: " + readUsage());
    }
}