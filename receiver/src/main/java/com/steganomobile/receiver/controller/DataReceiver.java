package com.steganomobile.receiver.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.view.CcService;

public class DataReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Const.ACTION_START_RECEIVER_CC.equals(intent.getAction())) {
            Intent myIntent = new Intent(context, CcService.class);
            context.startService(myIntent.replaceExtras(intent));
        }
    }
}
