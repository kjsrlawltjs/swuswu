package com.steganomobile.receiver.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.cc.CcImpl;

public class SyncReceiver extends BroadcastReceiver implements Sync {

    private static final String TAG = SyncReceiver.class.getSimpleName();
    private CcImpl cc;

    public SyncReceiver(CcImpl cc) {
        this.cc = cc;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();

        // Broadcast Receiver Sync
        if (action != null && action.contains(Const.SYNC_RECEIVER)) {
            if (cc != null) {
                cc.runCc(action);
            }
        }
    }

    public CcImpl getCc() {
        return cc;
    }

    public void finish(Context context) {
        cc.clearCc();
        cc.getCollector().finish(context);
    }
}
